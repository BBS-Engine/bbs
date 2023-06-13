package mchorse.bbs.core.input;

import mchorse.bbs.core.keybinds.KeybindManager;
import mchorse.bbs.graphics.window.Window;
import org.lwjgl.glfw.GLFW;

/**
 * Keyboard input class
 * 
 * This class is responsible for handling keyboard input. It can be used to 
 * check whether the key is down at the moment (in loops) or attach one key 
 * handler which will be called when a key was pressed, repeated or released.
 */
public class KeyboardInput
{
    public final IKeyHandler handler;
    public final KeybindManager keybinds = new KeybindManager();

    public KeyboardInput(IKeyHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Initiate the key callback 
     */
    public void init()
    {
        long win = Window.getWindow();

        GLFW.glfwSetKeyCallback(win, (window, key, scancode, action, mods) ->
        {
            if (this.handler != null)
            {
                this.handler.handleKey(key, scancode, action, mods);
            }
        });

        GLFW.glfwSetCharCallback(win, (window, key) ->
        {
            if (this.handler != null)
            {
                this.handler.handleTextInput(key);
            }
        });
    }
}