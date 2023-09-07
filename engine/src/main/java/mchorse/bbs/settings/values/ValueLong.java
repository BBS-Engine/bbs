package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.LongType;
import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueNumber;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.math.MathUtils;

import java.util.Arrays;
import java.util.List;

public class ValueLong extends BaseValueNumber<Long> implements IValueUIProvider
{
    public ValueLong(String id, Long defaultValue)
    {
        this(id, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public ValueLong(String id, Long defaultValue, Long min, Long max)
    {
        super(id, defaultValue, min, max);
    }

    @Override
    protected Long clamp(Long value)
    {
        return MathUtils.clamp(value, this.min, this.max);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UITrackpad trackpad = UIValueFactory.longUI(this, null);

        trackpad.w(90);

        return Arrays.asList(UIValueFactory.column(trackpad, this));
    }

    @Override
    public BaseType toData()
    {
        return new LongType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isNumeric())
        {
            this.value = data.asNumeric().longValue();
        }
    }

    @Override
    public String toString()
    {
        return Long.toString(this.value);
    }
}