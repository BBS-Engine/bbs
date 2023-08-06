package mchorse.bbs.voxel;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.data.storage.DataFileStorage;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.voxel.tilesets.BlockSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureManager implements IDisposable
{
    public static final String SCHEMATIC = ".schematic";

    public File folder;

    private Map<String, ChunkDisplay> cachedStructures = new HashMap<>();

    public StructureManager(File folder)
    {
        if (folder != null)
        {
            this.folder = folder;
            this.folder.mkdirs();
        }
    }

    @Override
    public void delete()
    {
        for (ChunkDisplay display : this.cachedStructures.values())
        {
            if (display != null)
            {
                display.delete();
            }
        }

        this.cachedStructures.clear();
    }

    public List<String> getIds()
    {
        return this.getIds(true);
    }

    public List<String> getIds(boolean includeSchematics)
    {
        File[] files = this.folder.listFiles();

        if (files == null)
        {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>();

        for (File file : files)
        {
            String name = file.getName();

            if (name.endsWith(".dat"))
            {
                list.add(name.substring(0, name.length() - 4));
            }
            else if (includeSchematics && name.endsWith(SCHEMATIC))
            {
                list.add(name);
            }
        }

        return list;
    }

    public Chunk load(String id, BlockSet blockSet)
    {
        File file = this.getFile(id);

        if (!file.exists())
        {
            return null;
        }

        try
        {
            MapType map = (MapType) new DataFileStorage(file).read();
            Chunk chunk = new Chunk(map.getInt("w"), map.getInt("h"), map.getInt("d"), blockSet.air);

            chunk.fromData(map.get("blocks"), blockSet);

            return chunk;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void save(String id, Chunk chunk)
    {
        MapType map = new MapType();

        map.putInt("w", chunk.w);
        map.putInt("h", chunk.h);
        map.putInt("d", chunk.d);
        map.put("blocks", chunk.toData());

        try
        {
            new DataFileStorage(this.getFile(id)).write(map);

            ChunkDisplay display = this.cachedStructures.remove(id);

            if (display != null)
            {
                display.delete();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ChunkDisplay getCachedChunk(String id, RenderingContext context, ChunkBuilder builder)
    {
        if (this.cachedStructures.containsKey(id))
        {
            return this.cachedStructures.get(id);
        }

        Chunk chunk = this.load(id, builder.models);

        if (chunk != null)
        {
            ChunkDisplay display = new ChunkDisplay(null, chunk, 0, 0, 0);

            builder.build(context, display, null);
            this.cachedStructures.put(id, display);

            return display;
        }

        this.cachedStructures.put(id, null);

        return null;
    }

    private File getFile(String id)
    {
        return new File(this.folder, id + ".dat");
    }
}