package mchorse.bbs.core.input;

import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;

/**
 * Key handler interface
 */
public interface IKeyHandler
{
    /**
     * Handle key event. See 
     * {@link GLFWKeyCallbackI#invoke(long, int, int, int, int)} for 
     * more information 
     */
    public boolean handleKey(int key, int scancode, int action, int mods);

    /**
     * Handle text input event See {@link GLFWCharCallback#invoke(long, int)} 
     * for more information.
     */
    public void handleTextInput(int key);
}