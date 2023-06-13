package mchorse.bbs.vox.data;

public class Vox
{
    public int w;
    public int h;
    public int d;

    public int[] voxels;

    public int toIndex(int x, int y, int z)
    {
        return x + y * this.w + z * this.w * this.h;
    }

    public boolean has(int x, int y, int z)
    {
        return x >= 0 && y >= 0 && z >= 0 && x < this.w && y < this.h && z < this.d && this.voxels[this.toIndex(x, y, z)] != 0;
    }

    public void set(int x, int y, int z, int block)
    {
        int index = this.toIndex(x, y, z);

        if (index < 0 || index >= this.w * this.h * this.d)
        {
            return;
        }

        this.voxels[index] = block;
    }
}