package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.items.ItemEntry;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.user.global.IScriptItems;
import mchorse.bbs.utils.resources.LinkUtils;

public class ScriptItems implements IScriptItems
{
    private ScriptBBS factory;

    public ScriptItems(ScriptBBS factory)
    {
        this.factory = factory;
    }

    @Override
    public ItemStack create(MapType data)
    {
        return ItemStack.create(data);
    }

    @Override
    public ItemStack create(String itemId, int count)
    {
        ItemEntry entry = BBS.getItems().get(LinkUtils.create(itemId));

        return entry == null ? ItemStack.EMPTY : new ItemStack(entry, count);
    }

    @Override
    public ItemStack createData(String string)
    {
        return this.create(this.factory.getData().map(string));
    }
}