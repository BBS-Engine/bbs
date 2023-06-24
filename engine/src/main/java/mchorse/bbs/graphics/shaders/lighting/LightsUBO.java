package mchorse.bbs.graphics.shaders.lighting;

import mchorse.bbs.graphics.ubo.UBO;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class LightsUBO extends UBO
{
    private final List<Light> lights = new ArrayList<Light>();
    private ByteBuffer buffer = MemoryUtil.memAlloc((int) this.size());

    public LightsUBO(int unit)
    {
        super(unit);
    }

    @Override
    protected long size()
    {
        /* Size explanation:
         *
         * - Length = 4 bytes for int + 12 bytes padding
         * - 20 lights *
         *     - Position = 12 bytes for vec3 + 4 bytes padding
         *     - Color = 12 bytes for vec3
         *     - Distance = 4 bytes for float
         *
         * std140 is weird, man...
         */
        return 16 + (8 * 4) * 20;
    }

    public void clear()
    {
        this.lights.clear();
    }

    public void addLight(Light light)
    {
        this.lights.add(light);
    }

    public void submitLights()
    {
        int size = this.lights.size();

        this.buffer.clear();
        this.buffer.putInt(size);
        this.buffer.putFloat(0);
        this.buffer.putFloat(0);
        this.buffer.putFloat(0);

        for (Light light : lights)
        {
            this.buffer.putFloat(light.position.x);
            this.buffer.putFloat(light.position.y);
            this.buffer.putFloat(light.position.z);
            this.buffer.putFloat(0);
            this.buffer.putFloat(light.color.r);
            this.buffer.putFloat(light.color.g);
            this.buffer.putFloat(light.color.b);
            this.buffer.putFloat(light.distance);
        }

        this.buffer.flip();

        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.id);
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, this.buffer);
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);

        this.lights.clear();
    }
}