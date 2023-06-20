package mchorse.bbs.ui.camera.clips.widgets;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UIBitToggle extends UIElement
{
    public static final IKey PLUS = IKey.raw(" + ");
    private int value;
    public List<Bit> bits = new ArrayList<Bit>();
    public Consumer<Integer> callback;

    public UIBitToggle(Consumer<Integer> callback)
    {
        super();

        this.callback = callback;

        this.h(20);
    }

    public UIBitToggle all()
    {
        return this.point().angles();
    }

    public UIBitToggle point()
    {
        this.bits.add(new Bit(UIKeys.X, Colors.RED));
        this.bits.add(new Bit(UIKeys.Y, Colors.GREEN));
        this.bits.add(new Bit(UIKeys.Z, Colors.BLUE));

        return this;
    }

    public UIBitToggle angles()
    {
        this.bits.add(new Bit(UIKeys.CAMERA_PANELS_YAW, Colors.YELLOW));
        this.bits.add(new Bit(UIKeys.CAMERA_PANELS_PITCH, Colors.CYAN));
        this.bits.add(new Bit(UIKeys.CAMERA_PANELS_ROLL, Colors.MAGENTA));
        this.bits.add(new Bit(UIKeys.CAMERA_PANELS_FOV, Colors.A50));

        return this;
    }

    public int getValue()
    {
        return this.value;
    }

    public void setValue(int value)
    {
        this.value = value;

        this.updateTooltip();
    }

    private void updateTooltip()
    {
        List<IKey> keys = new ArrayList<IKey>();

        for (int i = 0; i < this.bits.size(); i++)
        {
            Bit bit = this.bits.get(i);

            if (((this.value >> i) & 0b1) == 1)
            {
                keys.add(bit.label);
                keys.add(PLUS);
            }
        }

        if (keys.isEmpty())
        {
            this.removeTooltip();
        }
        else
        {
            keys.remove(keys.size() - 1);

            this.tooltip(IKey.comp(keys));
        }
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context.mouseX, context.mouseY) && context.mouseButton == 0)
        {
            int index = (context.mouseX - this.area.x) / (this.area.w / this.bits.size());

            this.value ^= 1 << index;

            if (this.callback != null)
            {
                this.callback.accept(this.value);
            }

            this.updateTooltip();

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        this.area.render(context.batcher, Colors.A50);

        int size = this.bits.size();
        int w = this.area.w / size;
        int hovered = -1;

        for (int i = 0; i < size; i++)
        {
            int x = this.area.x + w * i;
            boolean isSelected = ((this.value >> i) & 0x1) == 1;
            boolean isHover = this.area.isInside(context.mouseX, context.mouseY) && (context.mouseX - this.area.x) / w == i;
            int right = i == size - 1 ? this.area.ex() : x + w;

            if (isHover)
            {
                hovered = i;
            }

            Bit bit = this.bits.get(i);

            if (isSelected)
            {
                context.batcher.box(x, this.area.y, right, this.area.y + this.area.h, Colors.mulRGB(bit.color, isHover ? 0.8F : 1F));
            }
            else if (isHover)
            {
                context.batcher.box(x, this.area.y, right, this.area.y + this.area.h, Colors.mulRGB(bit.color, 0.2F));
            }

            if (!isSelected && i != 6)
            {
                context.batcher.box(right - 1, this.area.y, right, this.area.y + this.area.h, Colors.A50);
            }
        }

        context.batcher.outline(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.A50);

        if (hovered >= 0)
        {
            Bit bit = this.bits.get(hovered);
            String label = bit.label.get();

            context.batcher.textCard(context.font, label, this.area.mx(context.font.getWidth(label)), this.area.my(context.font.getHeight()));
        }
    }

    public static class Bit
    {
        public IKey label;
        public int color;

        public Bit(IKey label, int color)
        {
            this.label = label;
            this.color = Colors.A100 | color;
        }
    }
}