package mchorse.bbs.graphics.shaders.uniforms;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class UniformMatrix4s extends Uniform
{
    private List<Matrix4f> value = new ArrayList<>();

    public UniformMatrix4s(String name)
    {
        super(name);
    }

    public void set(List<Matrix4f> matrices)
    {
        this.value.clear();
        this.value.addAll(matrices);

        this.setChanged();
    }

    @Override
    protected void submitUniform()
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = stack.mallocFloat(this.value.size() * 16);

            for (int i = 0, c = this.value.size(); i < c; i++)
            {
                this.value.get(i).get(16 * i, buffer);
            }

            GL20.glUniformMatrix4fv(this.uniform, false, buffer);
        }
    }
}