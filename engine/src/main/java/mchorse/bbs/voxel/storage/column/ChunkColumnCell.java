package mchorse.bbs.voxel.storage.column;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
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

public class ChunkColumnCell extends ChunkCell
{
    public final ChunkDisplay[] displays;

    public ChunkColumnCell(ChunkManager manager, int x, int y, int z, int h)
    {
        super(manager);

        int s = manager.s;

        this.displays = new ChunkDisplay[h];

        for (int i = 0; i < this.displays.length; i++)
        {
            this.displays[i] = new ChunkDisplay(this, new Chunk(s, manager.builder.models.air), x, y + i, z);
        }

        this.bounds.setPosition(x * s, y * s, z * s).setSize(s, h * s, s);
    }

    @Override
    public void dirty()
    {
        for (ChunkDisplay display : this.displays)
        {
            display.dirty();
        }
    }

    @Override
    public void pushDirty()
    {
        for (ChunkDisplay display : this.displays)
        {
            display.cacheDirty();

            display.dirty = false;
        }
    }

    @Override
    public void popDirty()
    {
        for (ChunkDisplay display : this.displays)
        {
            if (display.wasDirty)
            {
                display.dirty();
            }
        }
    }

    @Override
    public ChunkDisplay getDisplay(int x, int y, int z)
    {
        int index = (y - this.bounds.y) / this.manager.s;

        return index >= 0 && index < this.displays.length ? this.displays[index] : null;
    }

    @Override
    public IBlockVariant getBlock(int x, int y, int z)
    {
        x -= this.bounds.x;
        y -= this.bounds.y;
        z -= this.bounds.z;

        int index = y / this.manager.s;

        if (index >= 0 && index < this.displays.length)
        {
            y = y - index * this.manager.s;

            return this.displays[index].chunk.getBlock(x, y, z);
        }

        return this.manager.builder.models.air;
    }

    @Override
    public boolean setBlockLocal(int x, int y, int z, IBlockVariant block, boolean priority)
    {
        int index = y / this.manager.s;

        if (index >= 0 && index < this.displays.length)
        {
            y = y - index * this.manager.s;

            ChunkDisplay display = this.displays[index];
            IBlockVariant old = display.chunk.getBlock(x, y, z);

            if (old != block)
            {
                display.dirty(priority);
                display.chunk.setBlock(x, y, z, block);
                this.saveLater();
            }

            return old != block;
        }

        return false;
    }

    @Override
    public void render(MatrixStack stack, Shader shader)
    {
        for (ChunkDisplay display : this.displays)
        {
            if (display.display != null)
            {
                CommonShaderAccess.setModelView(shader, stack);

                display.render();
            }

            stack.translate(0, this.manager.s, 0);
        }
    }

    @Override
    public void copy(ChunkCell cell)
    {
        ChunkDisplay[] displays = ((ChunkColumnCell) cell).displays;

        for (int i = 0; i < displays.length; i++)
        {
            this.displays[i] = displays[i];
        }
    }

    @Override
    public void delete()
    {
        for (ChunkDisplay display : this.displays)
        {
            display.delete();
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        ListType blocks = new ListType();

        for (ChunkDisplay display : this.displays)
        {
            blocks.add(display.chunk.toData());
        }

        data.put("blocks", blocks);
    }

    @Override
    public void fromData(EntityArchitect architect, MapType data)
    {
        super.fromData(architect, data);

        int i = 0;

        for (BaseType type : data.getList("blocks"))
        {
            if (i > this.displays.length)
            {
                break;
            }

            this.displays[i].chunk.fromData(type, this.manager.builder.models);

            i += 1;
        }
    }
}