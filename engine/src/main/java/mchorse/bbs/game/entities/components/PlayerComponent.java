package mchorse.bbs.game.entities.components;

import mchorse.bbs.world.entities.components.Component;
import org.lwjgl.glfw.GLFWGamepadState;

import java.nio.FloatBuffer;

public class PlayerComponent extends Component
{
    /* Joystick options */
    public final float[] prevSticks = new float[10];
    public final float[] sticks = new float[10];

    @Override
    public void preUpdate()
    {
        for (int i = 0; i < this.prevSticks.length; i++)
        {
            this.prevSticks[i] = this.sticks[i];
        }

        super.preUpdate();
    }

    public void updateJoystick(GLFWGamepadState state)
    {
        FloatBuffer buffer = state.axes();

        this.sticks[0] = buffer.get();
        this.sticks[1] = buffer.get();
        this.sticks[2] = buffer.get();
        this.sticks[3] = buffer.get();
        this.sticks[4] = buffer.get();
        this.sticks[5] = buffer.get();
    }
}