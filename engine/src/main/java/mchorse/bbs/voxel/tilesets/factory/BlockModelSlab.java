package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.Side;
import mchorse.bbs.voxel.blocks.BlockVariant;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.tilesets.models.BlockModel;

public class BlockModelSlab extends BlockModelAll
{
    @Override
    public BlockVariant getVariantForBuilding(RayTraceResult result)
    {
        if (result.normal.equals(Side.RIGHT.normal))
        {
            return this.variants.get(2);
        }
        else if (result.normal.equals(Side.LEFT.normal))
        {
            return this.variants.get(3);
        }
        else if (result.normal.equals(Side.FRONT.normal))
        {
            return this.variants.get(4);
        }
        else if (result.normal.equals(Side.BACK.normal))
        {
            return this.variants.get(5);
        }
        else if (result.normal.equals(Side.TOP.normal))
        {
            return this.variants.get(0);
        }

        return this.variants.get(1);
    }

    @Override
    public IBlockVariant rotateVariant(IBlockVariant variant, boolean clockwise)
    {
        if (variant.equals(this.variants.get(2)))
        {
            return clockwise ? this.variants.get(4) : this.variants.get(5);
        }
        else if (variant.equals(this.variants.get(3)))
        {
            return clockwise ? this.variants.get(5) : this.variants.get(4);
        }
        else if (variant.equals(this.variants.get(4)))
        {
            return clockwise ? this.variants.get(3) : this.variants.get(2);
        }
        else if (variant.equals(this.variants.get(5)) )
        {
            return clockwise ? this.variants.get(2) : this.variants.get(3);
        }

        return super.rotateVariant(variant, clockwise);
    }

    @Override
    public IBlockVariant flipVariant(IBlockVariant variant, Axis axis)
    {
        if (axis == Axis.X)
        {
            if (variant.equals(this.variants.get(2)))
            {
                return this.variants.get(3);
            }
            else if (variant.equals(this.variants.get(3)))
            {
                return this.variants.get(2);
            }
        }
        else if (axis == Axis.Y)
        {
            if (variant.equals(this.variants.get(0)))
            {
                return this.variants.get(1);
            }
            else if (variant.equals(this.variants.get(1)))
            {
                return this.variants.get(0);
            }
        }
        else if (axis == Axis.Z)
        {
            if (variant.equals(this.variants.get(4)))
            {
                return this.variants.get(5);
            }
            else if (variant.equals(this.variants.get(5)))
            {
                return this.variants.get(4);
            }
        }

        return super.flipVariant(variant, axis);
    }

    @Override
    public void compile()
    {
        BlockModel slabBottom = this.createModel();
        BlockModel slabTop = this.createModel();
        BlockModel slabLeft = this.createModel();
        BlockModel slabRight = this.createModel();
        BlockModel slabBack = this.createModel();
        BlockModel slabFront = this.createModel();

        BlockModelAll.cube(slabBottom, 0, 0, 0, 1, 0.5F, 1, this.allUV.x, this.allUV.y);
        slabBottom.collisionBox.setFromTwoPoints(0, 0, 0, 1, 0.5, 1);
        slabBottom.all = slabBottom.top;
        slabBottom.top = null;

        BlockModelAll.cube(slabTop, 0, 0.5F, 0, 1, 1, 1, this.allUV.x, this.allUV.y);
        slabTop.collisionBox.setFromTwoPoints(0, 0.5, 0, 1, 1, 1);
        slabTop.all = slabTop.bottom;
        slabTop.bottom = null;

        BlockModelAll.cube(slabLeft, 0, 0, 0, 0.5F, 1, 1, this.allUV.x, this.allUV.y);
        slabLeft.collisionBox.setFromTwoPoints(0, 0, 0, 0.5, 1, 1);
        slabLeft.all = slabLeft.right;
        slabLeft.right = null;

        BlockModelAll.cube(slabRight, 0.5F, 0, 0, 1, 1, 1, this.allUV.x, this.allUV.y);
        slabRight.collisionBox.setFromTwoPoints(0.5, 0, 0, 1, 1, 1);
        slabRight.all = slabRight.left;
        slabRight.left = null;

        BlockModelAll.cube(slabBack, 0, 0, 0, 1, 1, 0.5F, this.allUV.x, this.allUV.y);
        slabBack.collisionBox.setFromTwoPoints(0, 0, 0, 1, 1, 0.5);
        slabBack.all = slabBack.back;
        slabBack.back = null;

        BlockModelAll.cube(slabFront, 0, 0, 0.5F, 1, 1, 1, this.allUV.x, this.allUV.y);
        slabFront.collisionBox.setFromTwoPoints(0, 0, 0.5, 1, 1, 1);
        slabFront.all = slabFront.front;
        slabFront.front = null;

        this.models.add(0, slabBottom);
        this.models.add(1, slabTop);
        this.models.add(2, slabLeft);
        this.models.add(3, slabRight);
        this.models.add(4, slabBack);
        this.models.add(5, slabFront);
    }
}