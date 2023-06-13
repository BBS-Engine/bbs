package mchorse.bbs.voxel.tilesets.models;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import mchorse.bbs.voxel.tilesets.geometry.BlockGeometry;
import org.joml.Vector3i;

public class BlockModel
{
    private static Color temporary = new Color();

    public BlockModelFactory factory;

    public boolean collision = true;
    public boolean opaque = true;
    public boolean ao = true;
    public Color color = new Color(1F, 1F, 1F);
    public int lighting;

    /* Sided block geometry that gets culled depending on neighbors */
    public BlockGeometry top;
    public BlockGeometry bottom;
    public BlockGeometry right;
    public BlockGeometry left;
    public BlockGeometry front;
    public BlockGeometry back;

    /**
     * Block geometry that will be always present
     */
    public BlockGeometry all;

    public AABB collisionBox = new AABB(0, 0, 0, 1, 1, 1);

    public static BlockModel air()
    {
        BlockModel model = new BlockModel();

        model.opaque = model.ao = model.collision = false;

        return model;
    }

    public static BlockModel error()
    {
        BlockModel model = new BlockModel();

        model.ao = false;

        return model;
    }

    public void copy(BlockModel model)
    {
        this.collision = model.collision;
        this.opaque = model.opaque;
        this.ao = model.ao;
        this.color.copy(model.color);
        this.lighting = model.lighting;

        this.top = model.top;
        this.bottom = model.bottom;
        this.right = model.right;
        this.left = model.left;
        this.front = model.front;
        this.back = model.back;
        this.all = model.all;

        this.collisionBox = model.collisionBox;
    }

    public int build(VAOBuilder vao, ChunkBuilder builder, IBlockVariant block, int index, int nx, int ny, int nz, Vector3i edge)
    {
        temporary.copy(builder.color);
        builder.color.r *= this.color.r;
        builder.color.g *= this.color.g;
        builder.color.b *= this.color.b;

        if (this.top != null && edge.y <= 0)
        {
            BlockModel model = builder.block(nx, ny + 1, nz).getModel();

            if (!model.opaque || !this.isOverlapping(model.bottom, this.top, 0, -1, 0))
            {
                index = this.top.build(nx, ny, nz, index, block, builder, vao);
            }
        }

        if (this.bottom != null && edge.y >= 0)
        {
            BlockModel model = builder.block(nx, ny - 1, nz).getModel();

            if (!model.opaque || !this.isOverlapping(model.top, this.bottom, 0, 1, 0))
            {
                index = this.bottom.build(nx, ny, nz, index, block, builder, vao);
            }
        }

        if (this.right != null && edge.x <= 0)
        {
            BlockModel model = builder.block(nx + 1, ny, nz).getModel();

            if (!model.opaque || !this.isOverlapping(model.left, this.right, -1, 0, 0))
            {
                index = this.right.build(nx, ny, nz, index, block, builder, vao);
            }
        }

        if (this.left != null && edge.x >= 0)
        {
            BlockModel model = builder.block(nx - 1, ny, nz).getModel();

            if (!model.opaque || !this.isOverlapping(model.right, this.left, 1, 0, 0))
            {
                index = this.left.build(nx, ny, nz, index, block, builder, vao);
            }
        }

        if (this.front != null && edge.z <= 0)
        {
            BlockModel model = builder.block(nx, ny, nz + 1).getModel();

            if (!model.opaque || !this.isOverlapping(model.back, this.front, 0, 0, -1))
            {
                index = this.front.build(nx, ny, nz, index, block, builder, vao);
            }
        }

        if (this.back != null && edge.z >= 0)
        {
            BlockModel model = builder.block(nx, ny, nz - 1).getModel();

            if (!model.opaque || !this.isOverlapping(model.front, this.back, 0, 0, 1))
            {
                index = this.back.build(nx, ny, nz, index, block, builder, vao);
            }
        }

        index = this.all == null ? index : this.all.build(nx, ny, nz, index, block, builder, vao);

        builder.color.copy(temporary);

        return index;
    }

    private boolean isOverlapping(BlockGeometry target, BlockGeometry geometry, float x, float y, float z)
    {
        if (target == null || geometry == null)
        {
            return false;
        }

        return target.isOverlapping(geometry, x, y, z);
    }

    public void complete(BlockModelFactory factory)
    {
        this.factory = factory;

        if (this.top != null) this.top.complete();
        if (this.bottom != null) this.bottom.complete();
        if (this.right != null) this.right.complete();
        if (this.left != null) this.left.complete();
        if (this.front != null) this.front.complete();
        if (this.back != null) this.back.complete();
    }
}