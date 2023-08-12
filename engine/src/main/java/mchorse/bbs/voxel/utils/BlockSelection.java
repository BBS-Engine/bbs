package mchorse.bbs.voxel.utils;

import mchorse.bbs.utils.joml.Vectors;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class BlockSelection
{
    public final Vector3d a = new Vector3d();
    public final Vector3d b = new Vector3d();

    private Vector3i min = new Vector3i();
    private Vector3i max = new Vector3i();
    private Vector3i size = new Vector3i();

    public void move(int x, int y, int z)
    {
        this.a.add(x, y, z);
        this.b.add(x, y, z);

        this.recalculateRange();
    }

    public void set(Vector3d a, Vector3d b)
    {
        this.a.set(a.x, a.y, a.z);
        this.b.set(b.x, b.y, b.z);

        this.recalculateRange();
    }

    public void setA(Vector3i block)
    {
        this.a.set(block.x + 0.5D, block.y + 0.5D, block.z + 0.5D);

        this.recalculateRange();
    }

    public void setB(Vector3i block)
    {
        this.b.set(block.x + 0.5D, block.y + 0.5D, block.z + 0.5D);

        this.recalculateRange();
    }

    public void setPosition(int x, int y, int z)
    {
        this.a.set(x + 0.5D, y + 0.5D, z + 0.5D);
        this.b.set(this.a).add(this.size.x - 1, this.size.y - 1, this.size.z - 1);

        this.recalculateRange();
    }

    public void setSize(int x, int y, int z)
    {
        this.a.set(this.min.x, this.min.y, this.min.z).add(0.5D, 0.5D, 0.5D);
        this.b.set(this.a).add(x - 1, y - 1, z - 1);

        this.recalculateRange();
    }

    public void copy(BlockSelection selection)
    {
        this.a.set(selection.a);
        this.b.set(selection.b);

        this.recalculateRange();
    }

    private void recalculateRange()
    {
        Vector3d min = Vectors.min(this.a, this.b);
        Vector3d max = Vectors.max(this.a, this.b);

        this.min.set((int) Math.floor(min.x), (int) Math.floor(min.y), (int) Math.floor(min.z));
        this.max.set((int) Math.ceil(max.x), (int) Math.ceil(max.y), (int) Math.ceil(max.z));
        this.size.set(this.max.x - this.min.x, this.max.y - this.min.y, this.max.z - this.min.z);
    }

    public Vector3d getA()
    {
        return this.a;
    }

    public Vector3d getB()
    {
        return this.b;
    }

    public Vector3i getMin()
    {
        return this.min;
    }

    public Vector3i getMax()
    {
        return this.max;
    }

    public Vector3d getCenter()
    {
        return new Vector3d(this.a).add(this.b).div(2);
    }

    public Vector3i getSize()
    {
        return this.size;
    }

    public boolean isEmpty()
    {
        return this.size.lengthSquared() <= 0;
    }
}