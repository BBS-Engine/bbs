package mchorse.bbs.ui.framework.elements.buttons;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.ITextColoring;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

public class UIButton extends UIClickable<UIButton> implements ITextColoring
{
    public IKey label;

    public int textColor = Colors.WHITE;
    public boolean textShadow = true;

    public boolean custom;
    public int customColor;
    public boolean background = true;

    public UIButton(IKey label, Consumer<UIButton> callback)
    {
        super(callback);

        this.label = label;
        this.h(20);
    }

    public UIButton color(int color)
    {
        this.custom = true;
        this.customColor = color & Colors.RGB;

        return this;
    }

    public UIButton textColor(int color, boolean shadow)
    {
        this.textColor = color;
        this.textShadow = shadow;

        return this;
    }

    public UIButton background(boolean background)
    {
        this.background = background;

        return this;
    }

    @Override
    public void setColor(int color, boolean shadow)
    {
        this.textColor = color;
        this.textShadow = shadow;
    }

    @Override
    protected UIButton get()
    {
        return this;
    }

    @Override
    protected void renderSkin(UIContext context)
    {
        int color = Colors.A100 + (this.custom ? this.customColor : BBSSettings.primaryColor.get());

        if (this.hover)
        {
            color = Colors.mulRGB(color, 0.85F);
        }

        if (this.background)
        {
            this.area.render(context.batcher, color);
        }

        String label = context.font.limitToWidth(this.label.get(), this.area.w - 4);
        int x = this.area.mx(context.font.getWidth(label));
        int y = this.area.my(context.font.getHeight());

        context.batcher.text(label, x, y, Colors.mulRGB(this.textColor, this.hover ? 0.9F : 1F), this.textShadow);

        this.renderLockedArea(context);
    }
}