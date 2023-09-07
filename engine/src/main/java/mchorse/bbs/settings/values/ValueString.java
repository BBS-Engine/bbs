package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;

import java.util.Arrays;
import java.util.List;

public class ValueString extends BaseValueBasic<String> implements IValueUIProvider
{
    public ValueString(String id, String defaultValue)
    {
        super(id, defaultValue);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UITextbox textbox = UIValueFactory.stringUI(this, null);

        textbox.w(90);

        return Arrays.asList(UIValueFactory.column(textbox, this));
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (BaseType.isString(data))
        {
            this.value = data.asString();
        }
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}