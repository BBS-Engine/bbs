package mchorse.bbs.graphics.shaders.uniforms;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class UniformMatrix4 extends Uniform
{
    private Matrix4f value = new Matrix4f();

    public UniformMatrix4(String name)
    {
        super(name);
    }

    public void set(Matrix4f matrix)
    {
        if (this.value.equals(matrix))
        {
            return;
        }

        this.value.set(matrix);
        this.setChanged();
    }

    @Override
    protected void submitUniform()
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = stack.mallocFloat(16);

            this.value.get(buffer);
            GL20.glUniformMatrix4fv(this.uniform, false, buffer);
        }
    }
}