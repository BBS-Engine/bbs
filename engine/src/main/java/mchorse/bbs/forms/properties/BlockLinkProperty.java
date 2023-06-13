package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.voxel.blocks.BlockLink;

public class BlockLinkProperty extends BaseProperty<BlockLink>
{
    public BlockLinkProperty(Form form, String key, BlockLink value)
    {
        super(form, key, value);
    }

    @Override
    public void toData(MapType data)
    {
        if (this.value != null)
        {
            data.putString(this.getKey(), this.value.toString());
        }
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(BlockLink.create(data.getString(key)));
    }
}