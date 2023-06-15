package mchorse.bbs.ui.framework.elements.buttons;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.ITextColoring;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

public class UIToggle extends UIClickable<UIToggle> implements ITextColoring
{
    public IKey label;
    public int color = Colors.WHITE;
    public boolean textShadow = true;
    private boolean value;

    public UIToggle(IKey label, Consumer<UIToggle> callback)
    {
        this(label, false, callback);
    }

    public UIToggle(IKey label, boolean value, Consumer<UIToggle> callback)
    {
        super(callback);

        this.label = label;
        this.value = value;
        this.h(14);
    }

    @Override
    public void setColor(int color, boolean shadow)
    {
        this.color(color, shadow);
    }

    public UIToggle label(IKey label)
    {
        this.label = label;

        return this;
    }

    public UIToggle setValue(boolean value)
    {
        this.value = value;

        return this;
    }

    public UIToggle color(int color)
    {
        return this.color(color, true);
    }

    public UIToggle color(int color, boolean textShadow)
    {
        this.color = color;
        this.textShadow = textShadow;

        return this;
    }

    public boolean getValue()
    {
        return this.value;
    }

    @Override
    protected void click(int mouseWheel)
    {
        this.value = !this.value;

        super.click(mouseWheel);
    }

    @Override
    protected UIToggle get()
    {
        return this;
    }

    @Override
    protected void renderSkin(UIContext context)
    {
        FontRenderer font = context.font;
        String label = font.limitToWidth(this.label.get(), this.area.w - 18);

        context.batcher.text(label, this.area.x, this.area.my(font.getHeight()), this.color, this.textShadow);

        /* Draw toggle switch */
        int w = 16;
        int h = 10;
        int x = this.area.ex() - w - 2;
        int y = this.area.my();
        int color = BBSSettings.primaryColor.get();

        if (this.hover)
        {
            color = Colors.mulRGB(color, 0.85F);
        }

        /* Draw toggle background */
        context.batcher.box(x, y - h / 2, x + w, y - h / 2 + h, Colors.A100);
        context.batcher.box(x + 1, y - h / 2 + 1, x + w - 1, y - h / 2 + h - 1, Colors.A100 | (this.value ? color : (this.hover ? 0x3a3a3a : 0x444444)));

        if (this.value)
        {
            context.batcher.gradientHBox(x + 1, y - h / 2 + 1, x + w / 2, y - h / 2 + h - 1, Colors.setA(Colors.WHITE, 0.33F), Colors.setA(Colors.WHITE, 0F));
        }
        else
        {
            context.batcher.gradientHBox(x + w / 2, y - h / 2 + 1, x + w - 1, y - h / 2 + h - 1, 0, Colors.A50);
        }

        if (!this.isEnabled())
        {
            context.batcher.box(x, y - h / 2, x + w, y - h / 2 + h, Colors.A50);
        }

        x += this.value ? w - 2 : 2;

        /* Draw toggle switch */
        context.batcher.box(x - 4, y - 8, x + 4, y + 8, Colors.A100);
        context.batcher.box(x - 3, y - 7, x + 3, y + 7, Colors.WHITE);
        context.batcher.box(x - 2, y - 6, x + 3, y + 7, Colors.GRAY);
        context.batcher.box(x - 2, y - 6, x + 2, y + 6, Colors.LIGHTER_GRAY);

        if (!this.isEnabled())
        {
            context.batcher.box(x - 4, y - 8, x + 4, y + 8, Colors.A50);

            context.batcher.outlinedIcon(Icons.LOCKED, this.area.ex() - w / 2 - 2, y, 0.5F, 0.5F);
        }
    }
}