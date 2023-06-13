package mchorse.bbs.voxel.storage.data;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.utils.AABBi;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;

import java.util.HashSet;
import java.util.Set;

public abstract class ChunkCell implements IDisposable
{
    public ChunkManager manager;

    public final Set<Entity> entities = new HashSet<Entity>();

    public boolean generated;
    public boolean unsaved;
    public boolean removed;

    public final AABBi bounds = new AABBi();

    public ChunkCell(ChunkManager manager)
    {
        this.manager = manager;
    }

    public void addEntity(Entity entity)
    {
        this.entities.add(entity);

        this.saveLater();
    }

    public void removeEntity(Entity entity)
    {
        this.entities.remove(entity);

        this.saveLater();
    }

    /**
     * Mark this cell (and its display(s)) as dirty.
     */
    public abstract void dirty();

    /**
     * Cache temporarily dirty state of current chunk displays (and unflag dirty).
     */
    public abstract void pushDirty();

    /**
     * Used the cache value from {@link #pushDirty()} to determine whether chunk
     * displays need to be dirtied again.
     */
    public abstract void popDirty();

    /**
     * Get chunk display at given coordinates.
     */
    public abstract ChunkDisplay getDisplay(int x, int y, int z);

    /**
     * Get block at global block coordinates.
     */
    public abstract IBlockVariant getBlock(int x, int y, int z);

    /**
     * Set block at global block coordinates.
     */
    public boolean setBlock(int x, int y, int z, IBlockVariant block, boolean priority)
    {
        x -= this.bounds.x;
        y -= this.bounds.y;
        z -= this.bounds.z;

        return this.setBlockLocal(x, y, z, block, priority);
    }

    /**
     * Set block at local block coordinates relative to current cell's bounds.
     */
    public boolean setBlockLocal(int x, int y, int z, IBlockVariant block)
    {
        return this.setBlockLocal(x, y, z, block, false);
    }

    /**
     * Set block at local block coordinates with priority. Priority flag makes
     * any chunk displays added to the beginning of the dirty list (for priority
     * chunk building).
     */
    public abstract boolean setBlockLocal(int x, int y, int z, IBlockVariant block, boolean priority);

    /**
     * Set block lighting
     */
    public void setLighting(int x, int y, int z, int lighting)
    {
        ChunkDisplay display = this.getDisplay(x, y, z);

        if (display != null)
        {
            display.chunk.setLighting(x - display.x, y - display.y, z - display.z, lighting);
        }
    }

    /**
     * Mark as unsaved so the chunk would get saved later.
     */
    public void saveLater()
    {
        this.unsaved = true;
    }

    /**
     * Render this chunk cell.
     */
    public abstract void render(MatrixStack stack, Shader shader);

    /**
     * Copy over the data from another cell. This method is used during
     * chunk conversion from one format to another to quickly copy the data, instead of
     * manually copying block by block.
     */
    public abstract void copy(ChunkCell cell);

    public MapType toData()
    {
        MapType data = new MapType();

        this.toData(data);

        return data;
    }

    public void toData(MapType data)
    {
        ListType entities = new ListType();

        for (Entity entity : this.entities)
        {
            if (entity.canBeSaved)
            {
                entities.add(entity.toData());
            }
        }

        data.put("entities", entities);
    }

    public void fromData(EntityArchitect architect, MapType data)
    {
        if (data.has("entities"))
        {
            this.entities.clear();

            for (BaseType entityType : data.getList("entities"))
            {
                Entity e = architect.create((MapType) entityType);

                if (this.bounds.contains(e.basic.position))
                {
                    this.entities.add(e);
                }
            }
        }
    }
}