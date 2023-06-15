package mchorse.bbs.ui.framework.elements.buttons;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

public class UIIcon extends UIClickable<UIIcon>
{
    public Icon icon;
    public int iconColor = Colors.WHITE;
    public Icon hoverIcon;
    public int hoverColor = Colors.LIGHTEST_GRAY;

    public int disabledColor = 0x80404040;

    public UIIcon(Icon icon, Consumer<UIIcon> callback)
    {
        super(callback);

        this.icon = icon;
        this.hoverIcon = icon;
        this.wh(20, 20);
    }

    public UIIcon both(Icon icon)
    {
        this.icon = this.hoverIcon = icon;

        return this;
    }

    public UIIcon icon(Icon icon)
    {
        this.icon = icon;

        return this;
    }

    public UIIcon hovered(Icon icon)
    {
        this.hoverIcon = icon;

        return this;
    }

    public UIIcon iconColor(int color)
    {
        this.iconColor = color;

        return this;
    }

    public UIIcon hoverColor(int color)
    {
        this.hoverColor = color;

        return this;
    }

    public UIIcon disabledColor(int color)
    {
        this.disabledColor = color;

        return this;
    }

    @Override
    protected UIIcon get()
    {
        return this;
    }

    @Override
    protected void renderSkin(UIContext context)
    {
        Icon icon = this.hover ? this.hoverIcon : this.icon;
        int color = this.isEnabled() ? (this.hover ? this.hoverColor : this.iconColor) : this.disabledColor;

        context.batcher.icon(icon, color, this.area.mx(), this.area.my(), 0.5F, 0.5F);
    }
}