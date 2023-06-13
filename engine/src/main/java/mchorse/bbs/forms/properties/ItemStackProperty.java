package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;

public class ItemStackProperty extends BaseProperty<ItemStack>
{
    public ItemStackProperty(Form form, String key, ItemStack value)
    {
        super(form, key, value);
    }

    @Override
    public void toData(MapType data)
    {
        if (!this.value.isEmpty())
        {
            data.put(this.getKey(), this.value.toData());
        }
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        if (data.has(key))
        {
            this.set(ItemStack.create(data.getMap(key)));
        }
    }
}