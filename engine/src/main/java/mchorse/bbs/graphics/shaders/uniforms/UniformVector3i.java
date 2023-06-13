package mchorse.bbs.graphics.shaders.uniforms;

import org.joml.Vector3i;
import org.lwjgl.opengl.GL20;

public class UniformVector3i extends Uniform
{
    private static Vector3i temp = new Vector3i();

    private Vector3i value = new Vector3i();

    public UniformVector3i(String name)
    {
        super(name);
    }

    public void set(Vector3i vector)
    {
        this.set(vector.x, vector.y, vector.z);
    }

    public void set(int x, int y, int z)
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
        GL20.glUniform3i(this.uniform, this.value.x, this.value.y, this.value.z);
    }
}