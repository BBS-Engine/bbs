package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueDefault;
import mchorse.bbs.settings.values.base.IParseableValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.UIDataUtils;

import java.util.Arrays;
import java.util.List;

public class ValueContentType extends BaseValueDefault<String> implements IParseableValue, IValueUIProvider
{
    protected ContentType contentType;

    public ValueContentType(String id, ContentType type, String defaultValue)
    {
        super(id, defaultValue);

        this.contentType = type;
    }

    public ContentType getContentType()
    {
        return this.contentType;
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UIButton button = new UIButton(this.contentType.getPickLabel(), (b) ->
        {
            UIDataUtils.openPicker(ui.getContext(), this.contentType, this.value, this::set);
        });

        button.w(90);

        return Arrays.asList(UIValueFactory.column(button, this));
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
            this.set(((StringType) data).value);
        }
    }

    @Override
    public boolean parse(String value)
    {
        this.set(value);

        return true;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}