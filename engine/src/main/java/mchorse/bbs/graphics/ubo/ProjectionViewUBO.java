package mchorse.bbs.graphics.ubo;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class ProjectionViewUBO extends UBO
{
    public ProjectionViewUBO(int unit)
    {
        super(unit);
    }

    @Override
    protected long size()
    {
        return 128;
    }

    public void updateProjection(Matrix4f projection)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = stack.mallocFloat(16);

            projection.get(buffer);

            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.id);
            GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, buffer);
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
        }
    }

    public void updateView(Matrix4f view)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = stack.mallocFloat(16);

            view.get(buffer);

            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.id);
            GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 64, buffer);
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
        }
    }

    public void update(Matrix4f projection, Matrix4f view)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = stack.mallocFloat(32);

            projection.get(0, buffer);
            view.get(16, buffer);

            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.id);
            GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
        }
    }
}