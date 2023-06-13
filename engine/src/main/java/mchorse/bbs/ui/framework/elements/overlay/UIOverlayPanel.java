package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.events.UIOverlayCloseEvent;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class UIOverlayPanel extends UIElement
{
    public UILabel title;
    public UIElement icons;
    public UIIcon close;
    public UIElement content;

    private boolean moving;
    private int lastX;
    private int lastY;

    public UIOverlayPanel(IKey title)
    {
        super();

        this.title = UI.label(title);
        this.close = new UIIcon(Icons.CLOSE, (b) -> this.close());
        this.content = new UIElement();
        this.icons = new UIElement();

        this.title.relative(this).xy(10, 10).w(0.6F);
        this.close.wh(16, 16);
        this.icons.relative(this).x(1F, -7).y(6).anchorX(1F).row(0).reverse().resize().width(16).height(16);
        this.content.relative(this).xy(10, 28).w(1F, -20).h(1F, -28);

        this.icons.add(this.close);

        this.add(this.title, this.icons, this.content);

        this.blockInsideEvents();
    }

    public void onClose(Consumer<UIOverlayCloseEvent> callback)
    {
        this.events.register(UIOverlayCloseEvent.class, callback);
    }

    public void close()
    {
        UIElement parent = this.getParent();

        if (parent instanceof UIOverlay)
        {
            ((UIOverlay) parent).closeItself();
        }
    }

    public void confirm()
    {}

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.title.area.isInside(context))
        {
            this.moving = true;
            this.lastX = context.mouseX;
            this.lastY = context.mouseY;

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.moving = super.subMouseReleased(context);

        return false;
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (!context.isFocused() || Window.isCtrlPressed())
        {
            if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
            {
                this.close();

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_ENTER))
            {
                this.confirm();

                return true;
            }
        }

        return super.subKeyPressed(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.moving && (context.mouseX != this.lastX || context.mouseY != this.lastY))
        {
            this.flex.x.offset += context.mouseX - this.lastX;
            this.flex.y.offset += context.mouseY - this.lastY;

            this.getParent().resize();

            this.lastX = context.mouseX;
            this.lastY = context.mouseY;
        }

        this.renderBackground(context);

        super.render(context);
    }

    protected void renderBackground(UIContext context)
    {
        int color = BBSSettings.primaryColor.get();

        context.draw.dropShadow(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 10, Colors.A25 | color, color);
        this.area.render(context.draw, Colors.A100);

        if (this.title.area.isInside(context))
        {
            Icons.ALL_DIRECTIONS.render(context.draw, this.area.mx(), this.area.y + 14, Colors.GRAY, 0.5F, 0.5F);
        }
    }

    public void onClose()
    {
        this.events.emit(new UIOverlayCloseEvent(this));
    }
}