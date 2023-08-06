package mchorse.bbs.ui.dashboard.textures;

import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.textures.undo.PixelsUndo;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.utils.UICanvasEditor;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.rasterizers.LineRasterizer;
import mchorse.bbs.utils.resources.Pixels;
import mchorse.bbs.utils.undo.IUndo;
import mchorse.bbs.utils.undo.UndoManager;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class UIPixelsEditor extends UICanvasEditor
{
    public UITrackpad brightness;

    public UIElement toolbar;

    /* Tools */
    public UIColor primary;
    public UIColor secondary;

    public UIIcon undo;
    public UIIcon redo;

    private Texture temporary;
    private Pixels pixels;

    private boolean editing;
    private Color drawColor;
    private Vector2i lastPixel;

    private UndoManager<Pixels> undoManager;
    private PixelsUndo pixelsUndo;

    public UIPixelsEditor()
    {
        super();

        this.brightness = new UITrackpad();
        this.brightness.limit(0, 1).setValue(0.7);
        this.brightness.tooltip(UIKeys.TEXTURES_VIEWER_BRIGHTNESS, Direction.TOP);

        this.toolbar = new UIElement();
        this.toolbar.relative(this).w(1F).h(30).row(0).resize().padding(5);

        this.primary = new UIColor((c) -> {}).noLabel();
        this.primary.direction(Direction.RIGHT).w(20);
        this.secondary = new UIColor((c) -> {}).noLabel();
        this.secondary.direction(Direction.RIGHT).w(20);

        this.undo = new UIIcon(Icons.UNDO, (b) -> this.undo());
        this.undo.tooltip(UIKeys.TEXTURES_KEYS_UNDO, Direction.BOTTOM);
        this.redo = new UIIcon(Icons.REDO, (b) -> this.redo());
        this.redo.tooltip(UIKeys.TEXTURES_KEYS_REDO, Direction.BOTTOM);

        this.toolbar.add(this.primary, this.secondary.marginRight(10));
        this.toolbar.add(this.undo, this.redo);

        this.editor.add(this.brightness);
        this.add(this.toolbar);

        IKey category = UIKeys.TEXTURES_KEYS_CATEGORY;
        Supplier<Boolean> texture = () -> this.pixels != null;
        Supplier<Boolean> editing = () -> this.editing;

        this.keys().register(Keys.PIXEL_COPY, this::copyPixel).inside().active(texture).category(category);
        this.keys().register(Keys.PIXEL_SWAP, this::swapColors).inside().active(editing).category(category);
        this.keys().register(Keys.PIXEL_PICK, this::pickColor).inside().active(editing).category(category);
        this.keys().register(Keys.UNDO, this::undo).inside().active(editing).category(category);
        this.keys().register(Keys.REDO, this::redo).inside().active(editing).category(category);

        this.setEditing(false);
    }

    public Pixels getPixels()
    {
        return this.pixels;
    }

    protected void wasChanged()
    {}

    public boolean isEditing()
    {
        return this.editing;
    }

    public void toggleEditor()
    {
        this.setEditing(!this.editing);
    }

    public void setEditing(boolean editing)
    {
        this.editing = editing;

        this.primary.setColor(0);
        this.secondary.setColor(Colors.WHITE);

        this.toolbar.setVisible(editing);

        if (editing)
        {
            this.undoManager = new UndoManager<>();
            this.undoManager.setCallback(this::handleUndo);
        }
        else
        {
            this.undoManager = null;
        }

        this.pixelsUndo = null;
    }

    private void handleUndo(IUndo<Pixels> pixelsIUndo, boolean redo)
    {
        this.updateTexture();
    }

    private void copyPixel()
    {
        UIContext context = this.getContext();
        int pixelX = (int) Math.floor(this.scaleX.from(context.mouseX)) + this.w / 2;
        int pixelY = (int) Math.floor(this.scaleY.from(context.mouseY)) + this.h / 2;
        Color color = this.pixels.getColor(pixelX, pixelY);

        if (color != null)
        {
            Window.setClipboard(color.stringify(false));

            UIUtils.playClick();
        }
    }

    private void swapColors()
    {
        int swap = this.primary.picker.color.getRGBColor();

        this.primary.setColor(this.secondary.picker.color.getRGBColor());
        this.secondary.setColor(swap);
    }

    private void pickColor()
    {
        UIContext context = this.getContext();
        Vector2i pixel = this.getHoverPixel(context.mouseX, context.mouseY);
        Color color = this.pixels.getColor(pixel.x, pixel.y);

        if (color != null)
        {
            this.primary.setColor(color.getRGBColor());
        }
    }

    protected void updateTexture()
    {
        this.pixels.rewindBuffer();
        this.temporary.bind();
        this.temporary.updateTexture(this.pixels);
    }

    private void undo()
    {
        if (this.undoManager.undo(this.pixels))
        {
            UIUtils.playClick();
        }
    }

    private void redo()
    {
        if (this.undoManager.redo(this.pixels))
        {
            UIUtils.playClick();
        }
    }

    public void fillPixels(Pixels pixels)
    {
        this.lastPixel = null;

        if (this.temporary != null)
        {
            this.temporary.delete();
            this.temporary = null;
        }

        this.setEditing(false);

        this.pixels = pixels;

        if (pixels != null)
        {
            this.temporary = new Texture();
            this.temporary.setFilter(GL11.GL_NEAREST);

            this.updateTexture();
            this.setSize(pixels.width, pixels.height);
        }
    }

    @Override
    protected boolean isMouseButtonAllowed(int mouseButton)
    {
        return super.isMouseButtonAllowed(mouseButton) || mouseButton == 1;
    }

    @Override
    protected void startDragging(UIContext context)
    {
        super.startDragging(context);

        if (this.editing && (this.mouse == 0 || this.mouse == 1) && this.pixelsUndo == null)
        {
            this.pixelsUndo = new PixelsUndo();
            this.drawColor = this.mouse == 1 ? new Color(0, 0, 0, 0) : this.primary.picker.color;

            Vector2i pixel = this.getHoverPixel(context.mouseX, context.mouseY);

            this.pixelsUndo.setColor(this.pixels, pixel.x, pixel.y, this.drawColor);
            this.updateTexture();

            this.wasChanged();
        }
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        if (this.dragging && this.pixelsUndo != null)
        {
            Vector2i hoverPixel = this.getHoverPixel(context.mouseX, context.mouseY);

            if (Window.isShiftPressed() && this.lastPixel != null)
            {
                LineRasterizer rasterizer = new LineRasterizer(
                    new Vector2d(this.lastPixel.x, this.lastPixel.y),
                    new Vector2d(hoverPixel.x, hoverPixel.y)
                );
                Set<Vector2i> pixels = new HashSet<>();

                rasterizer.setupRange(0F, 1F, 1F / (float) this.lastPixel.distance(hoverPixel));
                rasterizer.solve(pixels);

                for (Vector2i pixel : pixels)
                {
                    this.pixelsUndo.setColor(this.pixels, pixel.x, pixel.y, this.drawColor);
                }

                this.updateTexture();
            }

            this.undoManager.pushUndo(this.pixelsUndo);

            this.pixelsUndo = null;
            this.lastPixel = hoverPixel;
        }

        return super.subMouseReleased(context);
    }

    @Override
    protected void renderBackground(UIContext context)
    {}

    @Override
    protected void renderCanvasFrame(UIContext context)
    {
        int x = -this.w / 2;
        int y = -this.h / 2;
        Area area = this.calculate(x, y, x + this.w, y + this.h);
        Texture texture = this.getRenderTexture(context);

        context.batcher.fullTexturedBox(texture, area.x, area.y, area.w, area.h);

        /* Draw current pixel */
        int pixelX = (int) Math.floor(this.scaleX.from(context.mouseX));
        int pixelY = (int) Math.floor(this.scaleY.from(context.mouseY));

        context.batcher.outline(
            (int) Math.round(this.scaleX.to(pixelX)), (int) Math.round(this.scaleY.to(pixelY)),
            (int) Math.round(this.scaleX.to(pixelX + 1)), (int) Math.round(this.scaleY.to(pixelY + 1)),
            Colors.A50
        );

        if (this.editing && this.dragging && (this.lastX != context.mouseX || this.lastY != context.mouseY) && (this.mouse == 0 || this.mouse == 1))
        {
            Vector2i last = this.getHoverPixel(this.lastX, this.lastY);
            Vector2i current = this.getHoverPixel(context.mouseX, context.mouseY);

            double distance = Math.max(new Vector2d(current.x, current.y).distance(last.x, last.y), 1);

            for (int i = 0; i <= distance; i++)
            {
                int xx = (int) Interpolations.lerp(last.x, current.x, i / distance);
                int yy = (int) Interpolations.lerp(last.y, current.y, i / distance);

                this.pixelsUndo.setColor(this.pixels, xx, yy, this.drawColor);
            }

            this.wasChanged();
            this.updateTexture();

            this.lastX = context.mouseX;
            this.lastY = context.mouseY;
        }
    }

    protected Texture getRenderTexture(UIContext context)
    {
        return this.temporary;
    }

    @Override
    protected void renderCheckboard(UIContext context, Area area)
    {
        int brightness = (int) (this.brightness.getValue() * 255);
        int color = Colors.setA(brightness << 16 | brightness << 8 | brightness, 1F);

        context.batcher.iconArea(Icons.CHECKBOARD, color, area.x, area.y, area.w, area.h);
    }

    @Override
    protected void renderForeground(UIContext context)
    {
        super.renderForeground(context);

        if (this.editing)
        {
            context.batcher.box(this.area.x, this.area.y, this.area.ex(), this.area.y + 10, Colors.A50);
            context.batcher.gradientVBox(this.area.x, this.area.y + 10, this.area.ex(), this.area.y + 30, Colors.A50, 0);
        }

        Vector2i pixel = this.getHoverPixel(context.mouseX, context.mouseY);
        Color color = this.pixels.getColor(pixel.x, pixel.y);

        int r = 0;
        int g = 0;
        int b = 0;
        int a = 0;

        if (color != null)
        {
            r = (int) Math.floor(color.r * 255);
            g = (int) Math.floor(color.g * 255);
            b = (int) Math.floor(color.b * 255);
            a = (int) Math.floor(color.a * 255);
        }

        String[] information = {
            this.pixels.width + "x" + this.pixels.height + " (" + pixel.x + ", " + pixel.y + ")",
            "\u00A75R\u00A7aG\u00A7cB\u00A7rA (" + r + ", " + g + ", " + b + ", " + a + ")",
        };

        int x = this.area.x + 10;
        int y = this.area.ey() - context.font.getHeight() - 10 - (information.length - 1)* 14;

        for (String line : information)
        {
            context.batcher.textCard(context.font, line, x, y);

            y += 14;
        }
    }
}