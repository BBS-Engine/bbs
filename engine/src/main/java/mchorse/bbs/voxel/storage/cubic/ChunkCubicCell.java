package mchorse.bbs.voxel.storage.cubic;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.world.entities.architect.EntityArchitect;

public class ChunkCubicCell extends ChunkCell
{
    public ChunkDisplay display;

    public ChunkCubicCell(ChunkManager manager, int x, int y, int z)
    {
        super(manager);

        int s = manager.s;

        this.display = new ChunkDisplay(this, new Chunk(s, manager.builder.models.air), x, y, z);

        this.bounds.setPosition(x * s, y * s, z * s).setSize(s, s, s);
    }

    @Override
    public void dirty()
    {
        this.display.dirty();
    }

    @Override
    public void pushDirty()
    {
        this.display.cacheDirty();

        this.display.dirty = false;
    }

    @Override
    public void popDirty()
    {
        if (this.display.wasDirty)
        {
            this.display.dirty();
        }
    }

    @Override
    public ChunkDisplay getDisplay(int x, int y, int z)
    {
        return this.display;
    }

    @Override
    public IBlockVariant getBlock(int x, int y, int z)
    {
        int dx = x - this.display.x;
        int dy = y - this.display.y;
        int dz = z - this.display.z;

        return this.display.chunk.getBlock(dx, dy, dz);
    }

    @Override
    public boolean setBlockLocal(int x, int y, int z, IBlockVariant block, boolean priority)
    {
        IBlockVariant old = this.display.chunk.getBlock(x, y, z);

        if (old != block)
        {
            this.display.dirty(priority);
            this.display.chunk.setBlock(x, y, z, block);
            this.saveLater();
        }

        return old != block;
    }

    @Override
    public void render(MatrixStack stack, Shader shader)
    {
        if (this.display.display == null)
        {
            return;
        }

        CommonShaderAccess.setModelView(shader, stack);

        this.display.render();
    }

    @Override
    public void copy(ChunkCell cell)
    {
        this.display = ((ChunkCubicCell) cell).display;
    }

    @Override
    public void delete()
    {
        this.display.delete();
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("blocks", this.display.chunk.toData());
    }

    @Override
    public void fromData(EntityArchitect architect, MapType data)
    {
        super.fromData(architect, data);

        this.display.chunk.fromData(data.get("blocks"), this.manager.builder.models);
    }
}