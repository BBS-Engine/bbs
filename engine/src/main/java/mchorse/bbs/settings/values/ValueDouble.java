package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.DoubleType;
import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueNumber;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.math.MathUtils;

import java.util.Arrays;
import java.util.List;

public class ValueDouble extends BaseValueNumber<Double> implements IValueUIProvider
{
    public ValueDouble(String id, Double defaultValue)
    {
        this(id, defaultValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ValueDouble(String id, Double defaultValue, Double min, Double max)
    {
        super(id, defaultValue, min, max);
    }

    @Override
    protected Double clamp(Double value)
    {
        return MathUtils.clamp(value, this.min, this.max);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UITrackpad trackpad = UIValueFactory.doubleUI(this, null);

        trackpad.w(90);

        return Arrays.asList(UIValueFactory.column(trackpad, this));
    }

    @Override
    public BaseType toData()
    {
        return new DoubleType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isNumeric())
        {
            this.set(data.asNumeric().doubleValue());
        }
    }

    @Override
    public String toString()
    {
        return Double.toString(this.value);
    }
}