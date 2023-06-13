package mchorse.bbs.graphics.shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class UniformInt extends Uniform
{
    private int value;

    public UniformInt(String name)
    {
        super(name);
    }

    public void set(boolean value)
    {
        this.set(value ? 1 : 0);
    }

    public void set(int value)
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
        GL20.glUniform1i(this.uniform, this.value);
    }
}