package mchorse.bbs.settings.values;

import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueDefault;
import mchorse.bbs.settings.values.base.IParseableValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;

import java.util.Arrays;
import java.util.List;

public class ValueBoolean extends BaseValueDefault<Boolean> implements IParseableValue, IValueUIProvider
{
    public ValueBoolean(String id)
    {
        this(id, false);
    }

    public ValueBoolean(String id, boolean defaultValue)
    {
        super(id, defaultValue);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UIToggle toggle = UIValueFactory.booleanUI(this, null);

        toggle.resetFlex();

        return Arrays.asList(toggle);
    }

    @Override
    public BaseType toData()
    {
        return new ByteType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isNumeric())
        {
            this.set(data.asNumeric().boolValue());
        }
    }

    @Override
    public boolean parse(String value)
    {
        if (value.equals("1"))
        {
            this.set(true);
        }
        else if (value.equals("0"))
        {
            this.set(false);
        }
        else
        {
            this.set(Boolean.parseBoolean(value));
        }

        return true;
    }

    @Override
    public String toString()
    {
        return Boolean.toString(this.value);
    }
}
