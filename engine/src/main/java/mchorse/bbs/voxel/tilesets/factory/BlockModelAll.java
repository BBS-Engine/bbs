package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.voxel.tilesets.geometry.QuadGeometry;
import mchorse.bbs.voxel.tilesets.models.BlockModel;

public class BlockModelAll extends BlockModelFactory
{
    public static final int DEFAULT_SIZE = 16;

    public static void cube(BlockModel model, float u, float v)
    {
        cube(model, 0, 0, 0, 1, 1, 1, u, v);
    }

    public static void cube(BlockModel model, float x1, float y1, float z1, float x2, float y2, float z2, float u, float v)
    {
        float w = (x2 - x1) * DEFAULT_SIZE;
        float h = (y2 - y1) * DEFAULT_SIZE;
        float d = (z2 - z1) * DEFAULT_SIZE;

        float ox = (1 - x2) * DEFAULT_SIZE;
        float oy = (1 - z2) * DEFAULT_SIZE;
        float u1 = ox + u;
        float v1 = oy + v;
        float u2 = ox + u + w;
        float v2 = oy + v + d;

        QuadGeometry top = new QuadGeometry(0, 1, 0);
        top.p1.set(x1, y2, z1);
        top.p2.set(x2, y2, z1);
        top.p3.set(x1, y2, z2);
        top.p4.set(x2, y2, z2);
        top.t1.set(u1, v1);
        top.t2.set(u2, v2);

        QuadGeometry bottom = new QuadGeometry(0, -1, 0);
        bottom.p1.set(x1, y1, z2);
        bottom.p2.set(x2, y1, z2);
        bottom.p3.set(x1, y1, z1);
        bottom.p4.set(x2, y1, z1);
        bottom.t1.set(u1, v1);
        bottom.t2.set(u2, v2);

        ox = (1 - z2) * DEFAULT_SIZE;
        oy = (1 - y2) * DEFAULT_SIZE;
        u1 = ox + u;
        v1 = oy + v;
        u2 = ox + u + d;
        v2 = oy + v + h;

        QuadGeometry right = new QuadGeometry(1, 0, 0);
        right.p1.set(x2, y1, z1);
        right.p2.set(x2, y1, z2);
        right.p3.set(x2, y2, z1);
        right.p4.set(x2, y2, z2);
        right.t1.set(u1, v1);
        right.t2.set(u2, v2);

        ox = z1 * DEFAULT_SIZE;
        oy = (1 - y2) * DEFAULT_SIZE;
        u1 = ox + u;
        v1 = oy + v;
        u2 = ox + u + d;
        v2 = oy + v + h;

        QuadGeometry left = new QuadGeometry(-1, 0, 0);
        left.p1.set(x1, y1, z2);
        left.p2.set(x1, y1, z1);
        left.p3.set(x1, y2, z2);
        left.p4.set(x1, y2, z1);
        left.t1.set(u1, v1);
        left.t2.set(u2, v2);

        ox = x1 * DEFAULT_SIZE;
        u1 = ox + u;
        v1 = oy + v;
        u2 = ox + u + w;
        v2 = oy + v + h;

        QuadGeometry front = new QuadGeometry(0, 0, 1);
        front.p1.set(x2, y1, z2);
        front.p2.set(x1, y1, z2);
        front.p3.set(x2, y2, z2);
        front.p4.set(x1, y2, z2);
        front.t1.set(u1, v1);
        front.t2.set(u2, v2);

        ox = (1 - x2) * DEFAULT_SIZE;
        u1 = ox + u;
        v1 = oy + v;
        u2 = ox + u + w;
        v2 = oy + v + h;

        QuadGeometry back = new QuadGeometry(0, 0, -1);
        back.p1.set(x1, y1, z1);
        back.p2.set(x2, y1, z1);
        back.p3.set(x1, y2, z1);
        back.p4.set(x2, y2, z1);
        back.t1.set(u1, v1);
        back.t2.set(u2, v2);

        model.top = top;
        model.bottom = bottom;
        model.right = right;
        model.left = left;
        model.front = front;
        model.back = back;
        top.ao = bottom.ao = right.ao = left.ao = front.ao = back.ao = true;
    }

    public BlockModelAll()
    {}

    @Override
    public void compile()
    {
        BlockModel model = this.createModel();

        cube(model, this.allUV.x, this.allUV.y);

        this.models.add(0, model);
    }
}