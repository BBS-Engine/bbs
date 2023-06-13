package mchorse.bbs.ui.tileset;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.utils.UICanvasEditor;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector2i;

public class UIUVEditor extends UICanvasEditor
{
    public UITrackpad u;
    public UITrackpad v;

    protected Vector2i uv;
    protected Link atlas;
    protected Runnable callback;

    private int tileW;
    private int tileH;

    public UIUVEditor(Link atlas, Runnable callback)
    {
        Texture texture = BBS.getTextures().getTexture(atlas);

        this.atlas = atlas;
        this.callback = callback;
        this.w = texture.width;
        this.h = texture.height;

        this.u = new UITrackpad((v) ->
        {
            this.uv.x = v.intValue();
            this.runCallback();
        }).integer();
        this.v = new UITrackpad((v) ->
        {
            this.uv.y = v.intValue();
            this.runCallback();
        }).integer();

        this.context((menu) ->
        {
            UIContext context = this.getContext();
            Vector2i hover = this.getHoverPixel(context.mouseX, context.mouseY);

            menu.action(Icons.SEARCH, UIKeys.UV_EDITOR_CONTEXT_VIEW, () -> this.jumpTo(this.uv, this.tileW, this.tileH, 3));
            menu.action(Icons.DOWNLOAD, UIKeys.UV_EDITOR_CONTEXT_PLACE, () -> this.uv.set(hover.x, hover.y));
        });

        UIElement element = UI.row(this.u, this.v);

        element.relative(this).x(10).y(1F, -30).w(1F, -20).h(20);

        this.add(element);
    }

    private void runCallback()
    {
        if (this.callback != null)
        {
            this.callback.run();
        }
    }

    public void setUV(Vector2i uv)
    {
        this.uv = uv;

        this.scaleX.set(0, 0.75F);
        this.scaleY.set(0, 0.75F);

        this.u.setValue(uv.x);
        this.v.setValue(uv.y);
    }

    public void setUVZoom(Vector2i uv, int w, int h, float zoom)
    {
        this.setUV(uv);

        this.tileW = w;
        this.tileH = h;

        this.jumpTo(uv, w, h, zoom);
    }

    private void jumpTo(Vector2i uv, int w, int h, float zoom)
    {
        this.scaleX.setZoom(zoom);
        this.scaleY.setZoom(zoom);
        this.scaleX.setShift(uv.x - (this.getWidth() - w) / 2);
        this.scaleY.setShift(uv.y - (this.getHeight() - h) / 2);
    }

    @Override
    protected void startDragging(UIContext context)
    {
        super.startDragging(context);

        if (this.mouse == 0)
        {
            this.lastT = this.uv.x;
            this.lastV = this.uv.y;
        }
    }

    @Override
    protected void dragging(UIContext context)
    {
        super.dragging(context);

        if (this.dragging && this.mouse == 0)
        {
            double dx = (context.mouseX - this.lastX) / this.scaleX.getZoom();
            double dy = (context.mouseY - this.lastY) / this.scaleY.getZoom();

            if (Window.isShiftPressed()) dx = 0;
            if (Window.isCtrlPressed()) dy = 0;

            this.uv.x = (int) (dx) + (int) this.lastT;
            this.uv.y = (int) (dy) + (int) this.lastV;

            if (Window.isAltPressed())
            {
                this.uv.x = MathUtils.toChunk(this.uv.x, 16) * 16;
                this.uv.y = MathUtils.toChunk(this.uv.y, 16) * 16;
            }

            this.u.setValue(this.uv.x);
            this.v.setValue(this.uv.y);

            this.runCallback();
        }
    }

    @Override
    protected void renderCanvasFrame(UIContext context)
    {
        Area area = this.calculate(-this.w / 2, -this.h / 2, -this.w / 2 + this.w, -this.h / 2 + this.h);

        context.render.getTextures().bind(this.atlas);
        context.draw.fullTexturedBox(area.x, area.y, area.w, area.h);

        area = this.calculate(-this.w / 2 + this.uv.x, -this.h / 2 + this.uv.y, -this.w / 2 + this.uv.x + 16, -this.h / 2 + this.uv.y + 16);

        context.draw.outline(area.x, area.y, area.ex(), area.ey(), Colors.setA(Colors.ACTIVE, 1F));
    }
}