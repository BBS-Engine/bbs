package mchorse.bbs.ui.utils.keys;

import org.lwjgl.glfw.GLFW;

public enum KeyAction
{
    PRESSED, RELEASED, REPEAT;

    public static KeyAction get(int action)
    {
        if (action == GLFW.GLFW_PRESS)
        {
            return PRESSED;
        }
        else if (action == GLFW.GLFW_REPEAT)
        {
            return REPEAT;
        }

        return RELEASED;
    }
}