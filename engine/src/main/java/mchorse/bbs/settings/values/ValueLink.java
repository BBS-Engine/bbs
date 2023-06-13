package mchorse.bbs.settings.values;

import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueDefault;
import mchorse.bbs.settings.values.base.IParseableValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.utils.resources.LinkUtils;

import java.util.Arrays;
import java.util.List;

public class ValueLink extends BaseValueDefault<Link> implements IParseableValue, IValueUIProvider
{
    public ValueLink(String id, Link defaultValue)
    {
        super(id, defaultValue);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UIButton pick = new UIButton(UIKeys.TEXTURE_PICK_TEXTURE, (button) ->
        {
            UITexturePicker.open(ui, this.value, this::set);
        });

        pick.w(90);

        return Arrays.asList(UIValueFactory.column(pick, this));
    }

    @Override
    public BaseType toData()
    {
        BaseType type = LinkUtils.toData(this.value);

        return type == null ? new MapType() : type;
    }

    @Override
    public void fromData(BaseType data)
    {
        this.value = LinkUtils.create(data);
    }

    @Override
    public boolean parse(String value)
    {
        this.set(value.isEmpty() ? null : LinkUtils.create(value));

        return true;
    }

    @Override
    public String toString()
    {
        return this.value == null ? "" : this.value.toString();
    }
}