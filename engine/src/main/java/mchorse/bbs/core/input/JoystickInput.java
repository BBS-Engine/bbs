package mchorse.bbs.core.input;

import mchorse.bbs.core.IDisposable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class JoystickInput implements IDisposable
{
    private boolean present;
    private IJoystickHandler handler;
    private GLFWGamepadState state;

    private int lastState;

    public static int getJoystickStateAsInt(GLFWGamepadState state)
    {
        ByteBuffer buttons = state.buttons();
        int i = 0;

        buttons.position(0);

        int gamepad = 0;

        while (buttons.position() < buttons.capacity())
        {
            gamepad = gamepad | (buttons.get() << i);

            i += 1;
        }

        return gamepad;
    }

    public JoystickInput(IJoystickHandler handler)
    {
        this.handler = handler;
        this.state = new GLFWGamepadState(MemoryUtil.memAlloc(GLFWGamepadState.SIZEOF));
    }

    public boolean isPresent()
    {
        return this.present;
    }

    public GLFWGamepadState getState()
    {
        return this.state;
    }

    public GLFWGamepadState getUpdatedState()
    {
        if (this.present)
        {
            GLFW.glfwGetGamepadState(GLFW.GLFW_JOYSTICK_1, this.state);
        }
        else
        {
            this.state.clear();
        }

        return this.state;
    }

    public boolean isButtonPressed(int button)
    {
        return this.state.buttons(button) > 0;
    }

    public void init()
    {
        GLFW.glfwSetJoystickCallback((jid, event) ->
        {
            if (jid == GLFW.GLFW_JOYSTICK_1)
            {
                if (event == GLFW.GLFW_CONNECTED)
                {
                    this.present = true;
                }
                else if (event == GLFW.GLFW_DISCONNECTED)
                {
                    this.present = false;
                }
            }
        });

        this.present = GLFW.glfwJoystickPresent(GLFW.GLFW_JOYSTICK_1);
    }

    public void update()
    {
        if (this.present && this.handler != null)
        {
            GLFWGamepadState joystickState = this.getUpdatedState();
            int state = getJoystickStateAsInt(joystickState);

            if (this.lastState != state)
            {
                for (int i = 0, c = joystickState.buttons().capacity(); i < c; i++)
                {
                    int button = (this.lastState >> i) & 0b1;
                    int lastButton = (state >> i) & 0b1;

                    if (button != lastButton)
                    {
                        this.handler.handleGamepad(i, button == 1 ? GLFW.GLFW_RELEASE : GLFW.GLFW_PRESS);
                    }
                }

                this.lastState = state;
            }
        }
    }

    @Override
    public void delete()
    {
        this.state.free();
    }
}