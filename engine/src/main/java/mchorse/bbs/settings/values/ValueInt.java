package mchorse.bbs.settings.values;

import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueNumber;
import mchorse.bbs.settings.values.base.IParseableValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.math.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValueInt extends BaseValueNumber<Integer> implements IParseableValue, IValueUIProvider
{
    private Subtype subtype = Subtype.INTEGER;
    private List<IKey> labels;

    public ValueInt(String id, Integer defaultValue)
    {
        this(id, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public ValueInt(String id, Integer defaultValue, Integer min, Integer max)
    {
        super(id, defaultValue, min, max);
    }

    @Override
    protected Integer clamp(Integer value)
    {
        return MathUtils.clamp(value, this.min, this.max);
    }

    public Subtype getSubtype()
    {
        return this.subtype;
    }

    public ValueInt subtype(Subtype subtype)
    {
        this.subtype = subtype;

        return this;
    }

    public ValueInt color()
    {
        return this.subtype(Subtype.COLOR);
    }

    public ValueInt colorAlpha()
    {
        return this.subtype(Subtype.COLOR_ALPHA);
    }

    public ValueInt modes(IKey... labels)
    {
        this.labels = new ArrayList<IKey>();
        Collections.addAll(this.labels, labels);

        return this.subtype(Subtype.MODES);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
        {
            UIColor color = UIValueFactory.colorUI(this, null);

            color.w(90);

            return Arrays.asList(UIValueFactory.column(color, this));
        }
        else if (this.subtype == Subtype.MODES)
        {
            UICirculate button = new UICirculate(null);

            for (IKey key : this.labels)
            {
                button.addLabel(key);
            }

            button.callback = (b) -> this.set(button.getValue());
            button.setValue(this.get());
            button.w(90);

            return Arrays.asList(UIValueFactory.column(button, this));
        }

        UITrackpad trackpad = UIValueFactory.intUI(this, null);

        trackpad.w(90);

        return Arrays.asList(UIValueFactory.column(trackpad, this));
    }

    @Override
    public BaseType toData()
    {
        return new IntType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isNumeric())
        {
            this.set(data.asNumeric().intValue());
        }
    }

    @Override
    public boolean parse(String value)
    {
        try
        {
            if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
            {
                this.set(Colors.parseWithException(value));
            }
            else
            {
                this.set(Integer.parseInt(value));
            }

            return true;
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public String toString()
    {
        if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
        {
            return "#" + Integer.toHexString(this.value);
        }

        return Integer.toString(this.value);
    }

    public static enum Subtype
    {
        INTEGER,
        COLOR,
        COLOR_ALPHA,
        MODES
    }
}