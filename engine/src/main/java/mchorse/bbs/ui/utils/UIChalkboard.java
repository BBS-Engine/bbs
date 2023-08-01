package mchorse.bbs.ui.utils;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.resources.Pixels;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

public class UIChalkboard extends UIElement
{
    private UIColor picker;

    private Pixels pixels;
    private Texture texture;

    private int color = Colors.setA(Colors.RED, 1F);
    private boolean drawing;

    private int lastX;
    private int lastY;

    public UIChalkboard()
    {
        super();

        this.texture = new Texture();
        this.texture.setFilter(GL11.GL_NEAREST);

        this.picker = new UIColor((c) -> this.color = c).withAlpha();
        this.picker.direction(Direction.TOP).withTarget(this).setColor(this.color);
        this.picker.relative(this).x(10).y(1F, -30).wh(60, 20);

        this.add(this.picker);

        this.keys().register(Keys.CHALKBOARD_CLEAR, this::resize);
    }

    public boolean isEnabled()
    {
        return BBSSettings.enableChalkboard.get() && this.isVisible();
    }

    /* Input handling */

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        if (!this.isEnabled())
        {
            return false;
        }

        if (context.mouseButton == 0)
        {
            this.drawing = true;

            this.lastX = context.mouseX;
            this.lastY = context.mouseY;

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected boolean subMouseReleased(UIContext context)
    {
        if (!this.isEnabled())
        {
            return false;
        }

        if (context.mouseButton == 0)
        {
            this.drawing = false;
        }

        return super.subMouseReleased(context);
    }

    @Override
    public void resize()
    {
        super.resize();

        if (this.pixels != null)
        {
            this.pixels.delete();
        }

        int scale = BBSSettings.getScale();

        this.pixels = Pixels.fromSize(this.area.w * scale, this.area.h * scale);
        this.pixels.rewindBuffer();
        this.texture.bind();
        this.texture.updateTexture(this.pixels);
    }

    @Override
    public void render(UIContext context)
    {
        int scale = BBSSettings.getScale();
        int x = context.mouseX;
        int y = context.mouseY;

        if (this.pixels != null)
        {
            if (this.drawing)
            {
                double distance = new Vector2d(x, y).distance(this.lastX, this.lastY);

                for (int i = 0; i < distance; i++)
                {
                    int xx = (int) (Interpolations.lerp(x * scale, this.lastX * scale, i / distance));
                    int yy = (int) (Interpolations.lerp(y * scale, this.lastY * scale, i / distance));

                    this.pixels.drawRect(xx - 1, yy - 2, 2, 4, this.color);
                    this.pixels.drawRect(xx - 2, yy - 1, 1, 2, this.color);
                    this.pixels.drawRect(xx + 1, yy - 1, 1, 2, this.color);
                }

                this.pixels.rewindBuffer();
                this.texture.bind();
                this.texture.updateTexture(this.pixels);

                this.lastX = x;
                this.lastY = y;
            }

            context.batcher.fullTexturedBox(this.texture, this.area.x, this.area.y, this.area.w, this.area.h);
        }

        context.batcher.box(x - 1, y - 1, x + 1, y + 1, this.color);

        super.render(context);
    }
}