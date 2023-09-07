package mchorse.bbs.settings.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UIKeybind;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.keys.KeyCombo;

import java.util.Arrays;
import java.util.List;

public class ValueKeyCombo extends BaseValueBasic<KeyCombo> implements IValueUIProvider
{
    public ValueKeyCombo(String id, KeyCombo combo)
    {
        super(id, combo);
    }

    @Override
    public void set(KeyCombo value)
    {
        this.preNotifyParent(this);
        this.value.copy(value);
        this.postNotifyParent(this);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UILabel label = UI.label(this.value.label, 0).labelAnchor(0, 0.5F);
        UIKeybind keybind = new UIKeybind(this::set);

        keybind.setKeyCombo(this.value);
        keybind.w(100);

        return Arrays.asList(UI.row(label, keybind).tooltip(this.value.label));
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (int key : this.value.keys)
        {
            list.addInt(key);
        }

        return list;
    }

    @Override
    public void fromData(BaseType data)
    {
        if (!data.isList())
        {
            return;
        }

        this.value.keys.clear();

        ListType list = data.asList();

        for (int i = 0; i < list.size(); i++)
        {
            this.value.keys.add(list.getInt(i));
        }
    }
}