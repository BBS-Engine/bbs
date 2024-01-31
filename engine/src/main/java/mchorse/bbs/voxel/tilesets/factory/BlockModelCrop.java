package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.voxel.tilesets.geometry.CombinedGeometry;
import mchorse.bbs.voxel.tilesets.geometry.QuadGeometry;
import mchorse.bbs.voxel.tilesets.models.BlockModel;

public class BlockModelCrop extends BlockModelFactory
{
    public BlockModelCrop()
    {
        this.opaque = this.ao = this.collision = false;
    }

    @Override
    public void compile()
    {
        BlockModel model = this.createModel();

        float firstRow = 0.333F;
        float secondRow = 0.666F;

        float x1 = 0;
        float y1 = 0;
        float z1 = 0;
        float x2 = 1;
        float y2 = 1;
        float z2 = 1;
        float u = this.allUV.x;
        float v = this.allUV.y;

        QuadGeometry xFront = model.createQuad(0, 1, 0);
        xFront.p1.set(x1, y1, firstRow);
        xFront.p2.set(x2, y1, firstRow);
        xFront.p3.set(x1, y2, firstRow);
        xFront.p4.set(x2, y2, firstRow);
        xFront.t1.set(u, v);
        xFront.t2.set(u + BlockModelAll.DEFAULT_SIZE, v + BlockModelAll.DEFAULT_SIZE);
        xFront.both = true;

        QuadGeometry xBack = model.createQuad(0, 1, 0);
        xBack.p1.set(x2, y1, secondRow);
        xBack.p2.set(x1, y1, secondRow);
        xBack.p3.set(x2, y2, secondRow);
        xBack.p4.set(x1, y2, secondRow);
        xBack.t1.set(u, v);
        xBack.t2.set(u + BlockModelAll.DEFAULT_SIZE, v + BlockModelAll.DEFAULT_SIZE);
        xBack.both = true;

        QuadGeometry zFront = model.createQuad(0, 1, 0);
        zFront.p1.set(firstRow, y1, z2);
        zFront.p2.set(firstRow, y1, z1);
        zFront.p3.set(firstRow, y2, z2);
        zFront.p4.set(firstRow, y2, z1);
        zFront.t1.set(u, v);
        zFront.t2.set(u + BlockModelAll.DEFAULT_SIZE, v + BlockModelAll.DEFAULT_SIZE);
        zFront.both = true;

        QuadGeometry zBack = model.createQuad(0, 1, 0);
        zBack.p1.set(secondRow, y1, z2);
        zBack.p2.set(secondRow, y1, z1);
        zBack.p3.set(secondRow, y2, z2);
        zBack.p4.set(secondRow, y2, z1);
        zBack.t1.set(u, v);
        zBack.t2.set(u + BlockModelAll.DEFAULT_SIZE, v + BlockModelAll.DEFAULT_SIZE);
        zBack.both = true;

        model.all = new CombinedGeometry(xFront, xBack, zFront, zBack);

        this.models.add(0, model);
    }
}