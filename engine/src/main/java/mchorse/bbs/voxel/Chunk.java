package mchorse.bbs.voxel;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteArrayType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.ShortArrayType;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.tilesets.BlockSet;

import java.util.Arrays;

/**
 * Chunk class
 * 
 * This class is responsible for storing chunk data
 */
public class Chunk implements IBlockAccessor
{
    /**
     * Array of block data 
     */
    protected IBlockVariant[] data;

    protected byte[] lighting;

    public final int w;
    public final int h;
    public final int d;

    /**
     * Default block represents emptiness (or air)
     */
    private IBlockVariant defaultBlock;

    public Chunk(int s, IBlockVariant defaultBlock)
    {
        this(s, s, s, defaultBlock);
    }

    /**
     * Initialize empty chunk data 
     */
    public Chunk(int w, int h, int d, IBlockVariant defaultBlock)
    {
        this.w = w;
        this.h = h;
        this.d = d;
        this.defaultBlock = defaultBlock;

        this.data = new IBlockVariant[w * h * d];
        this.lighting = new byte[w * h * d];

        Arrays.fill(this.data, defaultBlock);
    }

    /**
     * Get data array (don't modify)
     */
    public IBlockVariant[] getData()
    {
        return this.data;
    }

    /**
     * Set block at given coordinates
     */
    public void setBlock(int x, int y, int z, IBlockVariant block)
    {
        if (this.isOutside(x, y, z))
        {
            return;
        }

        this.data[x + y * this.w + z * this.w * this.h] = block;
    }

    public void setLighting(int x, int y, int z, int level)
    {
        if (this.isOutside(x, y, z))
        {
            return;
        }

        this.lighting[x + y * this.w + z * this.w * this.h] = (byte) level;
    }

    @Override
    public boolean hasBlock(int x, int y, int z)
    {
        return !this.isOutside(x, y, z) && !this.getBlock(x, y, z).isAir();
    }

    /**
     * Get block at given coordinate 
     */
    @Override
    public IBlockVariant getBlock(int x, int y, int z)
    {
        return this.isOutside(x, y, z) ? this.defaultBlock : this.data[x + y * this.w + z * this.w * this.h];
    }

    public int getLighting(int x, int y, int z)
    {
        return this.isOutside(x, y, z) ? 0 : this.lighting[x + y * this.w + z * this.w * this.h];
    }

    public boolean isOutside(int x, int y, int z)
    {
        return x < 0 || y < 0 || z < 0 || x >= this.w || y >= this.h || z >= this.d;
    }

    public BaseType toData()
    {
        int length = this.data.length;
        short[] shorts = new short[length];

        for (int i = 0; i < length; i++)
        {
            shorts[i] = (short) this.data[i].getGlobalId();
        }

        MapType data = new MapType();

        data.put("blocks", new ShortArrayType(shorts));
        data.put("lighting", new ByteArrayType(this.lighting));

        return data;
    }

    public void fromData(BaseType data, BlockSet set)
    {
        if (BaseType.is(data, BaseType.TYPE_SHORT_ARRAY))
        {
            this.fromData(((ShortArrayType) data).value, set);
        }
        else if (data.isMap())
        {
            MapType map = data.asMap();

            byte[] lighting = map.getByteArray("lighting");

            if (lighting.length == this.lighting.length)
            {
                this.lighting = lighting;
            }

            this.fromData(((ShortArrayType) map.get("blocks")).value, set);
        }
    }

    private void fromData(short[] shorts, BlockSet set)
    {
        int length = shorts.length;

        for (int i = 0; i < length; i++)
        {
            this.data[i] = set.get(shorts[i]);
        }
    }
}