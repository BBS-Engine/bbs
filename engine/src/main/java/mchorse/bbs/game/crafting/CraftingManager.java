package mchorse.bbs.game.crafting;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.manager.BaseManager;

import java.io.File;

public class CraftingManager extends BaseManager<CraftingTable>
{
    public CraftingManager(File folder)
    {
        super(folder);
    }

    @Override
    protected CraftingTable createData(String id, MapType mapType)
    {
        CraftingTable table = new CraftingTable();

        if (mapType != null)
        {
            table.fromData(mapType);
        }

        return table;
    }
}