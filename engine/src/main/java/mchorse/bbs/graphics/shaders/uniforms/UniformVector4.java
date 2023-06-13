package mchorse.bbs.graphics.shaders.uniforms;

import mchorse.bbs.utils.colors.Color;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

public class UniformVector4 extends Uniform
{
    private static Vector4f temp = new Vector4f();
    private static Color tempColor = new Color();

    private Vector4f value = new Vector4f();

    public UniformVector4(String name)
    {
        super(name);
    }

    public void reset()
    {
        this.set(1, 1, 1, 1);
    }

    public void set(Vector4f vector)
    {
        this.set(vector.x, vector.y, vector.z, vector.w);
    }

    public void set(int color)
    {
        this.set(tempColor.set(color));
    }

    public void set(Color color)
    {
        this.set(color.r, color.g, color.b, color.a);
    }

    public void set(float x, float y, float z, float w)
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
        GL20.glUniform4f(this.uniform, this.value.x, this.value.y, this.value.z, this.value.w);
    }
}