package mchorse.bbs.graphics.shaders.uniforms;

import org.joml.Vector4i;
import org.lwjgl.opengl.GL20;

public class UniformVector4i extends Uniform
{
    private static Vector4i temp = new Vector4i();

    private Vector4i value = new Vector4i();

    public UniformVector4i(String name)
    {
        super(name);
    }

    public void reset()
    {
        this.set(1, 1, 1, 1);
    }

    public void set(Vector4i vector)
    {
        this.set(vector.x, vector.y, vector.z, vector.w);
    }

    public void set(int x, int y, int z, int w)
    {
        if (this.value.equals(temp.set(x, y, z, w)))
        {
            return;
        }

        this.value.set(x, y, z, w);
        this.setChanged();
    }

    @Override
    protected void submitUniform()
    {
        GL20.glUniform4i(this.uniform, this.value.x, this.value.y, this.value.z, this.value.w);
    }
}