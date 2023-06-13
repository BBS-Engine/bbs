package mchorse.bbs.graphics.shaders.uniforms;

import org.joml.Matrix3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class UniformMatrix3 extends Uniform
{
    private Matrix3f value = new Matrix3f();

    public UniformMatrix3(String name)
    {
        super(name);
    }

    public void set(Matrix3f matrix)
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
            FloatBuffer buffer = stack.mallocFloat(9);

            this.value.get(buffer);
            GL20.glUniformMatrix3fv(this.uniform, false, buffer);
        }
    }
}