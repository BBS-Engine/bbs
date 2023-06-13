package mchorse.bbs.cubic.data.model;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class ModelVertex
{
    public Vector3f vertex = new Vector3f();
    public Vector2f uv = new Vector2f();

    public void set(Vector3f vertex, Vector2f uv, Model model)
    {
        this.vertex.set(vertex);
        this.uv.set(uv);

        this.vertex.x /= 16F;
        this.vertex.y /= 16F;
        this.vertex.z /= 16F;

        this.uv.x /= model.textureWidth;
        this.uv.y /= model.textureHeight;
    }
}
