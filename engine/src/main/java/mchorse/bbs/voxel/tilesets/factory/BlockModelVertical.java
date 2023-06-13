package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.voxel.tilesets.geometry.QuadGeometry;
import mchorse.bbs.voxel.tilesets.models.BlockModel;
import org.joml.Vector2i;

import java.util.Arrays;
import java.util.List;

public class BlockModelVertical extends BlockModelAll
{
    public final Vector2i topUV = new Vector2i();
    public final Vector2i bottomUV = new Vector2i();

    public BlockModelVertical()
    {}

    @Override
    public void compile()
    {
        super.compile();

        BlockModel model = this.models.list.get(0);

        QuadGeometry top = (QuadGeometry) model.top;
        QuadGeometry bottom = (QuadGeometry) model.bottom;

        top.t1.set(this.topUV.x, this.topUV.y);
        top.t2.set(this.topUV.x + DEFAULT_SIZE, this.topUV.y + DEFAULT_SIZE);

        bottom.t1.set(this.bottomUV.x, this.bottomUV.y);
        bottom.t2.set(this.bottomUV.x + DEFAULT_SIZE, this.bottomUV.y + DEFAULT_SIZE);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("top", DataStorageUtils.vector2iToData(this.topUV));
        data.put("bottom", DataStorageUtils.vector2iToData(this.bottomUV));
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.topUV.set(DataStorageUtils.vector2iFromData(data.getList("top")));
        this.bottomUV.set(DataStorageUtils.vector2iFromData(data.getList("bottom")));
    }
}