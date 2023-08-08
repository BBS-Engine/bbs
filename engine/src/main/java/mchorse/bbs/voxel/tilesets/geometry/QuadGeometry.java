package mchorse.bbs.voxel.tilesets.geometry;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class QuadGeometry extends BlockGeometry
{
    public Vector3f p1 = new Vector3f();
    public Vector3f p2 = new Vector3f();
    public Vector3f p3 = new Vector3f();
    public Vector3f p4 = new Vector3f();
    public Vector3f n = new Vector3f();
    public Vector2f t1 = new Vector2f();
    public Vector2f t2 = new Vector2f();
    public boolean both;
    public boolean ao;

    public Vector3f min;
    public Vector3f max;

    private Vector4f computedAO = new Vector4f(1);
    private Vector3f vertex = new Vector3f();

    public QuadGeometry(float x, float y, float z)
    {
        this.n.set(x, y, z);
    }

    @Override
    public void complete()
    {
        this.min = new Vector3f(
            this.min(this.p1.x, this.p2.x, this.p3.x, this.p4.x),
            this.min(this.p1.y, this.p2.y, this.p3.y, this.p4.y),
            this.min(this.p1.z, this.p2.z, this.p3.z, this.p4.z)
        );
        this.max = new Vector3f(
            this.max(this.p1.x, this.p2.x, this.p3.x, this.p4.x),
            this.max(this.p1.y, this.p2.y, this.p3.y, this.p4.y),
            this.max(this.p1.z, this.p2.z, this.p3.z, this.p4.z)
        );

        /* UV shift inward to avoid UV bleeding */
        float dx = Math.copySign(0.001F, this.t2.x - this.t1.x);
        float dy = Math.copySign(0.001F, this.t2.y - this.t1.y);

        this.t2.x -= dx;
        this.t2.y -= dy;
        this.t1.x += dx;
        this.t1.y += dy;
    }

    private float min(float a, float b, float c, float d)
    {
        return Math.min(d, Math.min(c, Math.min(b, a)));
    }

    private float max(float a, float b, float c, float d)
    {
        return Math.max(d, Math.max(c, Math.max(b, a)));
    }

    @Override
    public int build(int nx, int ny, int nz, int index, IBlockVariant block, ChunkBuilder builder, VAOBuilder vao, VBOAttributes attributes)
    {
        float tw = builder.models.atlasWidth;
        float th = builder.models.atlasHeight;

        if (this.ao)
        {
            this.computeAOs(builder, nx, ny, nz);
        }

        float ao1 = this.computedAO.x;
        float ao2 = this.computedAO.y;
        float ao3 = this.computedAO.z;
        float ao4 = this.computedAO.w;
        float f1 = this.getLightingFactor(this.p1, builder, nx, ny, nz);
        float f2 = this.getLightingFactor(this.p2, builder, nx, ny, nz);
        float f3 = this.getLightingFactor(this.p3, builder, nx, ny, nz);
        float f4 = this.getLightingFactor(this.p4, builder, nx, ny, nz);
        float r = builder.color.r;
        float g = builder.color.g;
        float b = builder.color.b;
        float a = builder.color.a;
        boolean ao = attributes == VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA;

        /* |_ - bottom left */
        vao.xyz(nx + this.p1.x, ny + this.p1.y, nz + this.p1.z)
            .normal(this.n.x, this.n.y, this.n.z)
            .uv(this.t2.x, this.t2.y, tw, th);

        if (ao) vao.xy(f1, 0);

        vao.rgba(ao1 * r, ao1 * g, ao1 * b, a);

        /* _| - bottom right */
        vao.xyz(nx + this.p2.x, ny + this.p2.y, nz + this.p2.z)
            .normal(this.n.x, this.n.y, this.n.z)
            .uv(this.t1.x, this.t2.y, tw, th);

        if (ao) vao.xy(f2, 0);

        vao.rgba(ao2 * r, ao2 * g, ao2 * b, a);

        /* |\ - top left */
        vao.xyz(nx + this.p3.x, ny + this.p3.y, nz + this.p3.z)
            .normal(this.n.x, this.n.y, this.n.z)
            .uv(this.t2.x, this.t1.y, tw, th);

        if (ao) vao.xy(f3, 0);

        vao.rgba(ao3 * r, ao3 * g, ao3 * b, a);

        /* /| - top right */
        vao.xyz(nx + this.p4.x, ny + this.p4.y, nz + this.p4.z)
            .normal(this.n.x, this.n.y, this.n.z)
            .uv(this.t1.x, this.t1.y, tw, th);

        if (ao) vao.xy(f4, 0);

        vao.rgba(ao4 * r, ao4 * g, ao4 * b, a);

        if (this.both)
        {
            vao.index(index).index(index + 3).index(index + 2);
            vao.index(index).index(index + 1).index(index + 3);

            vao.index(index + 2).index(index + 3).index(index);
            vao.index(index + 3).index(index + 1).index(index);
        }
        else
        {
            if (ao1 + ao4 > ao2 + ao3)
            {
                vao.index(index + 2).index(index + 3).index(index);
                vao.index(index + 3).index(index + 1).index(index);
            }
            else
            {
                vao.index(index + 2).index(index + 1).index(index);
                vao.index(index + 2).index(index + 3).index(index + 1);
            }
        }

        return index + 4;
    }

    private float getLightingFactor(Vector3f vertex, ChunkBuilder builder, int nx, int ny, int nz)
    {
        int x = this.round(vertex.x - 0.5F, this.n.x);
        int y = this.round(vertex.y - 0.5F, this.n.y);
        int z = this.round(vertex.z - 0.5F, this.n.z);

        int base = builder.lighting((int) (nx + this.n.x), (int) (ny + this.n.y), (int) (nz + this.n.z));

        this.vertex.set(this.n);

        if (x == 0) this.vertex.add(0, y, 0);
        else if (y == 0) this.vertex.add(x, 0, 0);
        else if (z == 0) this.vertex.add(x, 0, 0);

        int side1 = builder.block(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z).isAir()
            ? builder.lighting(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z)
            : base;

        this.vertex.set(this.n);

        if (x == 0) this.vertex.add(0, 0, z);
        else if (y == 0) this.vertex.add(0, 0, z);
        else if (z == 0) this.vertex.add(0, y, 0);

        int side2 = builder.block(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z).isAir()
            ? builder.lighting(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z)
            : base;

        this.vertex.set(this.n);
        this.vertex.add(x, y, z);

        int corner = builder.block(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z).isAir()
            ? builder.lighting(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z)
            : base;

        return (float) Interpolations.bilerp(0.5, 0.5, base, side1, side2, corner) / 15F;
    }

    private void computeAOs(ChunkBuilder builder, int nx, int ny, int nz)
    {
        this.computedAO.x = this.computeAO(builder, this.p1, nx, ny, nz);
        this.computedAO.y = this.computeAO(builder, this.p2, nx, ny, nz);
        this.computedAO.z = this.computeAO(builder, this.p3, nx, ny, nz);
        this.computedAO.w = this.computeAO(builder, this.p4, nx, ny, nz);
    }

    /**
     * Compute Ambient Occlusion
     *
     * @link https://0fps.net/2013/07/03/ambient-occlusion-for-minecraft-like-worlds/
     */
    private float computeAO(ChunkBuilder builder, Vector3f vertex, int nx, int ny, int nz)
    {
        int x = this.round(vertex.x - 0.5F, this.n.x);
        int y = this.round(vertex.y - 0.5F, this.n.y);
        int z = this.round(vertex.z - 0.5F, this.n.z);

        this.vertex.set(this.n);

        if (x == 0) this.vertex.add(0, y, 0);
        else if (y == 0) this.vertex.add(x, 0, 0);
        else if (z == 0) this.vertex.add(x, 0, 0);

        int side1 = builder.emitsAO(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z) ? 1 : 0;

        this.vertex.set(this.n);

        if (x == 0) this.vertex.add(0, 0, z);
        else if (y == 0) this.vertex.add(0, 0, z);
        else if (z == 0) this.vertex.add(0, y, 0);

        int side2 = builder.emitsAO(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z) ? 1 : 0;

        this.vertex.set(this.n);
        this.vertex.add(x, y, z);

        int corner = builder.emitsAO(nx + (int) this.vertex.x, ny + (int) this.vertex.y, nz + (int) this.vertex.z) ? 1 : 0;

        int finalAO;

        if (side1 > 0 && side2 > 0)
        {
            finalAO = 0;
        }
        else
        {
            finalAO = 3 - (side1 + side2 + corner);
        }

        if (finalAO == 3 && builder.emitsAO(nx + (int) this.n.x, ny + (int) this.n.y, nz + (int) this.n.z))
        {
            finalAO = 2;
        }

        return 1 * (finalAO / 3F * 0.5F + 0.5F);
    }

    private int round(float number, float normal)
    {
        if (normal != 0)
        {
            return 0;
        }

        return number > 0 ? 1 : (number < 0 ? -1 : 0);
    }

    @Override
    public boolean isOverlapping(BlockGeometry geometry, float x, float y, float z)
    {
        if (!(geometry instanceof QuadGeometry))
        {
            return false;
        }

        QuadGeometry quad = (QuadGeometry) geometry;

        float dX1 = quad.min.x - this.min.x + x;
        float dY1 = quad.min.y - this.min.y + y;
        float dZ1 = quad.min.z - this.min.z + z;
        float dX2 = quad.max.x - this.max.x + x;
        float dY2 = quad.max.y - this.max.y + y;
        float dZ2 = quad.max.z - this.max.z + z;

        float d1 = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
        float d2 = dX2 * dX2 + dY2 * dY2 + dZ2 * dZ2;

        return d1 < 0.05F && d2 < 0.05F;
    }
}