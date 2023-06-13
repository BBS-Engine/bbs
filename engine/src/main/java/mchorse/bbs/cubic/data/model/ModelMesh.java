package mchorse.bbs.cubic.data.model;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ModelMesh implements IMapSerializable
{
    public Vector3f origin = new Vector3f();
    public Vector3f rotate = new Vector3f();
    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public List<Vector2f> uvs = new ArrayList<Vector2f>();

    @Override
    public void fromData(MapType data)
    {
        this.vertices.clear();
        this.uvs.clear();

        this.origin.set(DataStorageUtils.vector3fFromData(data.getList("origin"), this.origin));
        this.rotate.set(DataStorageUtils.vector3fFromData(data.getList("rotate"), this.rotate));

        ListType vertices = data.getList("vertices");
        ListType uvs = data.getList("uvs");

        if (vertices.size() / 3 == uvs.size() / 2)
        {
            for (int i = 0, c = vertices.size() / 3; i < c; i++)
            {
                int indexV = i * 3;
                int indexU = i * 2;

                this.vertices.add(new Vector3f(vertices.getFloat(indexV), vertices.getFloat(indexV + 1), vertices.getFloat(indexV + 2)).add(this.origin));
                this.uvs.add(new Vector2f(uvs.getFloat(indexU), uvs.getFloat(indexU + 1)));
            }
        }
    }

    @Override
    public void toData(MapType data)
    {
        ListType vertices = new ListType();
        ListType uvs = new ListType();

        for (Vector3f v : this.vertices)
        {
            vertices.addFloat(v.x);
            vertices.addFloat(v.y);
            vertices.addFloat(v.z);
        }

        for (Vector2f v : this.uvs)
        {
            uvs.addFloat(v.x);
            uvs.addFloat(v.y);
        }

        data.put("origin", DataStorageUtils.vector3fToData(this.origin));
        data.put("rotate", DataStorageUtils.vector3fToData(this.rotate));
        data.put("vertices", vertices);
        data.put("uvs", uvs);
    }
}