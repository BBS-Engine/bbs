package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.voxel.tilesets.geometry.CombinedGeometry;
import mchorse.bbs.voxel.tilesets.geometry.QuadGeometry;
import mchorse.bbs.voxel.tilesets.models.BlockModel;

public class BlockModelPlant extends BlockModelFactory
{
    public BlockModelPlant()
    {
        this.opaque = this.ao = this.collision = false;
    }

    @Override
    public void compile()
    {
        BlockModel model = this.createModel();
        final float sin45half = (float) Math.sin(Math.PI / 4) / 2;
        float x1 = 0.5F - sin45half;
        float y1 = 0;
        float z1 = 0.5F - sin45half;
        float x2 = 0.5F + sin45half;
        float y2 = 1;
        float z2 = 0.5F + sin45half;
        float u = this.allUV.x;
        float v = this.allUV.y;

        QuadGeometry left = new QuadGeometry(0, 1, 0);
        left.p1.set(x1, y1, z1);
        left.p2.set(x2, y1, z2);
        left.p3.set(x1, y2, z1);
        left.p4.set(x2, y2, z2);
        left.t1.set(u, v);
        left.t2.set(u + BlockModelAll.DEFAULT_SIZE, v + BlockModelAll.DEFAULT_SIZE);
        left.both = true;

        QuadGeometry right = new QuadGeometry(0, 1, 0);
        right.p1.set(x2, y1, z1);
        right.p2.set(x1, y1, z2);
        right.p3.set(x2, y2, z1);
        right.p4.set(x1, y2, z2);
        right.t1.set(u, v);
        right.t2.set(u + BlockModelAll.DEFAULT_SIZE, v + BlockModelAll.DEFAULT_SIZE);
        right.both = true;

        model.all = new CombinedGeometry(left, right);

        this.models.add(0, model);
    }
}