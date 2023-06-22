package mchorse;

import mchorse.bbs.data.storage.DataFileStorage;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.packs.DataSourcePack;

import java.io.File;

public class Packer
{
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            return;
        }

        File assets = new File(args[0]);

        if (!assets.isDirectory())
        {
            System.err.println("Given path isn't a folder!");
            System.exit(1);

            return;
        }

        System.out.println("Packing: " + assets.getAbsolutePath());

        MapType map = DataSourcePack.pack(assets);
        File target = new File(assets, "assets.dat");

        if (!target.isFile() || target.delete())
        {
            if (new DataFileStorage(target).writeSilently(map))
            {
                System.out.println("Assets were packed to: " + target.getAbsolutePath());
            }
            else
            {
                System.out.println("There was an error packing or saving assets...");
            }
        }
        else
        {
            System.err.println("Couldn't delete the existing assets.dat! Try removing it manually...");
            System.exit(1);
        }
    }
}