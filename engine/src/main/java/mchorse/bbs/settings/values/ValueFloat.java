package mchorse.bbs.settings.values;

import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueNumber;
import mchorse.bbs.settings.values.base.IParseableValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.FloatType;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.math.MathUtils;

import java.util.Arrays;
import java.util.List;

public class ValueFloat extends BaseValueNumber<Float> implements IParseableValue, IValueUIProvider
{
    public ValueFloat(String id, Float defaultValue)
    {
        this(id, defaultValue, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public ValueFloat(String id, Float defaultValue, Float min, Float max)
    {
        super(id, defaultValue, min, max);
    }

    @Override
    protected Float clamp(Float value)
    {
        return MathUtils.clamp(value, this.min, this.max);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UITrackpad trackpad = UIValueFactory.floatUI(this, null);

        trackpad.w(90);

        return Arrays.asList(UIValueFactory.column(trackpad, this));
    }

    @Override
    public BaseType toData()
    {
        return new FloatType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isNumeric())
        {
            this.set(data.asNumeric().floatValue());
        }
    }

    @Override
    public boolean parse(String value)
    {
        try
        {
            this.set(Float.parseFloat(value));
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public String toString()
    {
        return Float.toString(this.value);
    }
}