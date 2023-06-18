package mchorse;

import mchorse.bbs.data.storage.DataFileStorage;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.packs.DataSourcePack;

import java.io.File;

public class Packer
{
    public static final String TARGET = "3rd-party";

    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            return;
        }

        File assets = new File(args[0]);
        MapType map = DataSourcePack.pack(new File(assets, TARGET));

        new DataFileStorage(new File(assets, "game/assets.dat")).writeSilently(map);
    }
}