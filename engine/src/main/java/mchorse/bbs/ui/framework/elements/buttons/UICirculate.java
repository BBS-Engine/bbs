package mchorse.bbs.ui.framework.elements.buttons;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class UICirculate extends UIClickable<UICirculate>
{
    public IKey label;

    public boolean custom;
    public int customColor;

    protected List<IKey> labels = new ArrayList<>();
    protected Set<Integer> disabled = new HashSet<>();
    protected int value = 0;

    public UICirculate(Consumer<UICirculate> callback)
    {
        super(callback);

        this.h(20);
    }

    public UICirculate color(int color)
    {
        this.custom = true;
        this.customColor = color & Colors.RGB;

        return this;
    }

    public List<IKey> getLabels()
    {
        return this.labels;
    }

    public void addLabel(IKey label)
    {
        if (this.labels.isEmpty())
        {
            this.label = label;
        }

        this.labels.add(label);
    }

    public void disable(int value)
    {
        if (this.disabled.size() < this.labels.size())
        {
            this.disabled.add(value);
        }
    }

    public int getValue()
    {
        return this.value;
    }

    public String getLabel()
    {
        return this.labels.get(this.value).get();
    }

    public void setValue(int value)
    {
        this.setValue(value, 1);
    }

    public void setValue(int value, int direction)
    {
        this.value = value;

        if (this.disabled.contains(value))
        {
            this.setValue(value + direction, direction);

            return;
        }

        if (this.value > this.labels.size() - 1)
        {
            this.value = 0;
        }

        if (this.value < 0)
        {
            this.value = this.labels.size() - 1;
        }

        this.label = this.labels.get(this.value);
    }

    @Override
    protected boolean isAllowed(int mouseButton)
    {
        return mouseButton == 0 || mouseButton == 1;
    }

    @Override
    protected void click(int mouseButton)
    {
        int direction = mouseButton == 0 ? 1 : -1;

        this.setValue(this.value + direction, direction);

        super.click(mouseButton);
    }

    @Override
    protected UICirculate get()
    {
        return this;
    }

    @Override
    protected void renderSkin(UIContext context)
    {
        int color = Colors.A100 | (this.custom ? this.customColor : BBSSettings.primaryColor.get());

        if (this.hover)
        {
            color = Colors.mulRGB(color, 0.85F);
        }

        this.area.render(context.batcher, color);

        String label = context.font.limitToWidth(this.label.get(), this.area.w - 4);
        int x = this.area.mx(context.font.getWidth(label));
        int y = this.area.my(context.font.getHeight());

        context.batcher.textShadow(label, x, y, Colors.mulRGB(Colors.WHITE, this.hover ? 0.9F : 1F));

        this.renderLockedArea(context);
    }
}
