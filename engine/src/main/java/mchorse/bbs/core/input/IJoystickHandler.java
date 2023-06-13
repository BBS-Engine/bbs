package mchorse.bbs.core.input;

public interface IJoystickHandler
{
    /**
     * Game pad button handler.
     *
     * @param button One of the gamepad buttons, see {@link org.lwjgl.glfw.GLFW#GLFW_GAMEPAD_BUTTON_A}.
     * @param action {@link org.lwjgl.glfw.GLFW#GLFW_PRESS} or {@link org.lwjgl.glfw.GLFW#GLFW_RELEASE}.
     */
    public boolean handleGamepad(int button, int action);
}