package mchorse.bbs.graphics.shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform
{
    private float value;

    public UniformFloat(String name)
    {
        super(name);
    }

    public void set(float value)
    {
        if (this.value == value)
        {
            return;
        }

        this.value = value;

        this.setChanged();
    }

    @Override
    protected void submitUniform()
    {
        GL20.glUniform1f(this.uniform, this.value);
    }
}