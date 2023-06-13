package mchorse.bbs.graphics.shaders.uniforms;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class UniformVector2 extends Uniform
{
    private static Vector2f temp = new Vector2f();

    private Vector2f value = new Vector2f();

    public UniformVector2(String name)
    {
        super(name);
    }

    public void set(Vector2f vector)
    {
        this.set(vector.x, vector.y);
    }

    public void set(float x, float y)
    {
        if (this.value.equals(temp.set(x, y)))
        {
            return;
        }

        this.value.set(temp);
        this.setChanged();
    }

    @Override
    protected void submitUniform()
    {
        GL20.glUniform2f(this.uniform, this.value.x, this.value.y);
    }
}