package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.Side;
import mchorse.bbs.voxel.blocks.BlockVariant;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.tilesets.geometry.CombinedGeometry;
import mchorse.bbs.voxel.tilesets.models.BlockModel;

public class BlockModelStair extends BlockModelAll
{
    @Override
    public BlockVariant getVariantForBuilding(RayTraceResult result)
    {
        if (result.normal.equals(Side.RIGHT.normal))
        {
            return this.variants.get(0);
        }
        else if (result.normal.equals(Side.LEFT.normal))
        {
            return this.variants.get(1);
        }
        else if (result.normal.equals(Side.FRONT.normal))
        {
            return this.variants.get(2);
        }
        else if (result.normal.equals(Side.BACK.normal))
        {
            return this.variants.get(3);
        }

        double dx = result.origin.x - result.hit.x;
        double dz = result.origin.z - result.hit.z;

        if (Math.abs(dx) > Math.abs(dz))
        {
            return this.variants.get(dx < 0 ? 1 : 0);
        }

        return this.variants.get(dz < 0 ? 3 : 2);
    }

    @Override
    public IBlockVariant rotateVariant(IBlockVariant variant, boolean clockwise)
    {
        if (variant.equals(this.variants.get(0)))
        {
            return clockwise ? this.variants.get(2) : this.variants.get(3);
        }
        else if (variant.equals(this.variants.get(1)))
        {
            return clockwise ? this.variants.get(3) : this.variants.get(2);
        }
        else if (variant.equals(this.variants.get(2)))
        {
            return clockwise ? this.variants.get(1) : this.variants.get(0);
        }
        else if (variant.equals(this.variants.get(3)))
        {
            return clockwise ? this.variants.get(0) : this.variants.get(1);
        }

        return super.rotateVariant(variant, clockwise);
    }

    @Override
    public IBlockVariant flipVariant(IBlockVariant variant, Axis axis)
    {
        if (axis == Axis.X)
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
            if (variant.equals(this.variants.get(2)))
            {
                return this.variants.get(3);
            }
            else if (variant.equals(this.variants.get(3)))
            {
                return this.variants.get(2);
            }
        }

        return super.flipVariant(variant, axis);
    }

    @Override
    public void compile()
    {
        BlockModel stairLeft = this.createModel();
        BlockModel stairRight = this.createModel();
        BlockModel stairBack = this.createModel();
        BlockModel stairFront = this.createModel();
        BlockModel temporary = this.createModel();
        BlockModel full = this.createModel();

        BlockModelAll.cube(full, this.allUV.x, this.allUV.y);

        /* Left stair (negative X) */
        BlockModelAll.cube(stairLeft, 0, 0, 0, 1F, 0.5F, 1F, this.allUV.x, this.allUV.y);
        stairLeft.all = stairLeft.top;
        stairLeft.top = null;

        BlockModelAll.cube(temporary, 0, 0.5F, 0, 0.5F, 1F, 1F, this.allUV.x, this.allUV.y);

        stairLeft.top = temporary.top;
        stairLeft.left = full.left;
        stairLeft.right = new CombinedGeometry(stairLeft.right, temporary.right);
        stairLeft.front = new CombinedGeometry(stairLeft.front, temporary.front);
        stairLeft.back = new CombinedGeometry(stairLeft.back, temporary.back);

        /* Right stair (positive X) */
        BlockModelAll.cube(stairRight, 0, 0, 0, 1F, 0.5F, 1F, this.allUV.x, this.allUV.y);
        stairRight.all = stairRight.top;
        stairRight.top = null;

        BlockModelAll.cube(temporary, 0.5F, 0.5F, 0, 1F, 1F, 1F, this.allUV.x, this.allUV.y);

        stairRight.top = temporary.top;
        stairRight.left = new CombinedGeometry(stairRight.left, temporary.left);
        stairRight.right = full.right;
        stairRight.front = new CombinedGeometry(stairRight.front, temporary.front);
        stairRight.back = new CombinedGeometry(stairRight.back, temporary.back);

        /* Back stair (negative Z) */
        BlockModelAll.cube(stairBack, 0, 0, 0, 1F, 0.5F, 1F, this.allUV.x, this.allUV.y);
        stairBack.all = stairBack.top;
        stairBack.top = null;

        BlockModelAll.cube(temporary, 0, 0.5F, 0, 1F, 1F, 0.5F, this.allUV.x, this.allUV.y);

        stairBack.top = temporary.top;
        stairBack.left = new CombinedGeometry(stairBack.left, temporary.left);
        stairBack.right = new CombinedGeometry(stairBack.right, temporary.right);
        stairBack.front = new CombinedGeometry(stairBack.front, temporary.front);
        stairBack.back = full.back;
        
        /* Front stair (positive Z) */
        BlockModelAll.cube(stairFront, 0, 0, 0, 1F, 0.5F, 1F, this.allUV.x, this.allUV.y);
        stairFront.all = stairFront.top;
        stairFront.top = null;

        BlockModelAll.cube(temporary, 0, 0.5F, 0.5F, 1F, 1F, 1F, this.allUV.x, this.allUV.y);

        stairFront.top = temporary.top;
        stairFront.left = new CombinedGeometry(stairFront.left, temporary.left);
        stairFront.right = new CombinedGeometry(stairFront.right, temporary.right);
        stairFront.front = full.front;
        stairFront.back = new CombinedGeometry(stairFront.back, temporary.back);

        this.models.add(0, stairLeft);
        this.models.add(1, stairRight);
        this.models.add(2, stairBack);
        this.models.add(3, stairFront);
    }
}