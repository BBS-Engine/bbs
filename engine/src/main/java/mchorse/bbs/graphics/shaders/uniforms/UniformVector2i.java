package mchorse.bbs.graphics.shaders.uniforms;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL20;

public class UniformVector2i extends Uniform
{
    private static Vector2i temp = new Vector2i();

    private Vector2i value = new Vector2i();

    public UniformVector2i(String name)
    {
        super(name);
    }

    public void set(Vector2i vector)
    {
        this.set(vector.x, vector.y);
    }

    public void set(int x, int y)
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
        GL20.glUniform2i(this.uniform, this.value.x, this.value.y);
    }
}