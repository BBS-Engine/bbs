package mchorse.bbs.graphics.shaders.uniforms;

import mchorse.bbs.utils.colors.Color;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class UniformVector3 extends Uniform
{
    private static Vector3f temp = new Vector3f();

    private Vector3f value = new Vector3f();

    public UniformVector3(String name)
    {
        super(name);
    }

    public void set(Vector3f vector)
    {
        this.set(vector.x, vector.y, vector.z);
    }

    public void set(Color color)
    {
        this.set(color.r, color.g, color.b);
    }

    public void set(float x, float y, float z)
    {
        if (this.value.equals(temp.set(x, y, z)))
        {
            return;
        }

        this.value.set(temp);
        this.setChanged();
    }

    @Override
    protected void submitUniform()
    {
        GL20.glUniform3f(this.uniform, this.value.x, this.value.y, this.value.z);
    }
}