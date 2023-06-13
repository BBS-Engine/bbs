package mchorse.bbs.cubic.data.model;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ModelQuad
{
    public List<ModelVertex> vertices = new ArrayList<ModelVertex>();
    public Vector3f normal = new Vector3f();

    public ModelQuad normal(float x, float y, float z)
    {
        this.normal.set(x, y, z);

        return this;
    }

    public ModelQuad vertex(float x, float y, float z, float u, float v)
    {
        ModelVertex vertex = new ModelVertex();

        vertex.vertex.set(x, y, z);
        vertex.uv.set(u, v);
        this.vertices.add(vertex);

        return this;
    }
}