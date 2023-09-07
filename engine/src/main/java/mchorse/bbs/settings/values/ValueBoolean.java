package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueDefault;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;

import java.util.Arrays;
import java.util.List;

public class ValueBoolean extends BaseValueDefault<Boolean> implements IValueUIProvider
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
    public String toString()
    {
        return Boolean.toString(this.value);
    }
}
