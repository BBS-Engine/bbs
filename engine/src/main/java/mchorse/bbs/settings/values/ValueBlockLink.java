package mchorse.bbs.settings.values;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.world.UIBlockVariant;
import mchorse.bbs.voxel.blocks.BlockLink;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.tilesets.BlockSet;

import java.util.Arrays;
import java.util.List;

public class ValueBlockLink extends BaseValueBasic<BlockLink> implements IValueUIProvider
{
    public ValueBlockLink(String id)
    {
        super(id, null);
    }

    public IBlockVariant get(BlockSet set)
    {
        return set.getVariant(this.value);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        IBlockVariant value = this.get(ui.getContext().menu.bridge.get(IBridgeWorld.class).getWorld().chunks.builder.models);

        UIElement element = new UIElement();
        UIBlockVariant variant = new UIBlockVariant((v) ->
        {
            this.set(v.isAir() ? null : BlockLink.create(v.getLink().toString()));
        });

        variant.setVariant(value);
        element.row(0).preferred(0).height(24);
        element.add(UIValueFactory.label(this), variant);

        return Arrays.asList(UIValueFactory.commetTooltip(element, this));
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.value == null ? "" : this.value.toString());
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isString())
        {
            this.value = BlockLink.create(data.asString());
        }
    }
}