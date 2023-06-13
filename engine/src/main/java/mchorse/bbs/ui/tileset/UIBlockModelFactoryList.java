package mchorse.bbs.ui.tileset;

import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;

import java.util.List;
import java.util.function.Consumer;

public class UIBlockModelFactoryList extends UIList<BlockModelFactory>
{
    public UIBlockModelFactoryList(Consumer<List<BlockModelFactory>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }
}