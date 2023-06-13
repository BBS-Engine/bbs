package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.voxel.tilesets.geometry.QuadGeometry;
import mchorse.bbs.voxel.tilesets.models.BlockModel;
import org.joml.Vector2i;

public class BlockModelEach extends BlockModelVertical
{
    public final Vector2i backUV = new Vector2i();
    public final Vector2i rightUV = new Vector2i();
    public final Vector2i leftUV = new Vector2i();

    public BlockModelEach()
    {}

    @Override
    public void compile()
    {
        super.compile();

        BlockModel model = this.models.list.get(0);

        QuadGeometry back = (QuadGeometry) model.back;
        QuadGeometry top = (QuadGeometry) model.top;
        QuadGeometry bottom = (QuadGeometry) model.bottom;
        QuadGeometry right = (QuadGeometry) model.right;
        QuadGeometry left = (QuadGeometry) model.left;

        back.t1.set(this.backUV.x, this.backUV.y);
        back.t2.set(this.backUV.x + DEFAULT_SIZE, this.backUV.y + DEFAULT_SIZE);

        top.t1.set(this.topUV.x, this.topUV.y);
        top.t2.set(this.topUV.x + DEFAULT_SIZE, this.topUV.y + DEFAULT_SIZE);

        bottom.t1.set(this.bottomUV.x, this.bottomUV.y);
        bottom.t2.set(this.bottomUV.x + DEFAULT_SIZE, this.bottomUV.y + DEFAULT_SIZE);

        right.t1.set(this.rightUV.x, this.rightUV.y);
        right.t2.set(this.rightUV.x + DEFAULT_SIZE, this.rightUV.y + DEFAULT_SIZE);

        left.t1.set(this.leftUV.x, this.leftUV.y);
        left.t2.set(this.leftUV.x + DEFAULT_SIZE, this.leftUV.y + DEFAULT_SIZE);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.remove("all");
        data.put("front", DataStorageUtils.vector2iToData(this.allUV));
        data.put("back", DataStorageUtils.vector2iToData(this.backUV));
        data.put("right", DataStorageUtils.vector2iToData(this.rightUV));
        data.put("left", DataStorageUtils.vector2iToData(this.leftUV));
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.backUV.set(DataStorageUtils.vector2iFromData(data.getList("back")));
        this.rightUV.set(DataStorageUtils.vector2iFromData(data.getList("right")));
        this.leftUV.set(DataStorageUtils.vector2iFromData(data.getList("left")));

        if (data.has("front"))
        {
            this.allUV.set(DataStorageUtils.vector2iFromData(data.getList("front")));
        }
    }
}