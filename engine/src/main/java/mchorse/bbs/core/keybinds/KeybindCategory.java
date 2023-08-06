package mchorse.bbs.core.keybinds;

import mchorse.bbs.core.input.IKeyHandler;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Keybind category
 * 
 * This class is responsible for handling keybinds
 */
public class KeybindCategory implements IKeyHandler
{
    /**
     * Identifier of the category 
     */
    public final String id;

    /**
     * List of keybinds 
     */
    public List<Keybind> keybinds = new ArrayList<>();

    /**
     * Active supplier, it allows dynamically specify whether the category is active or not
     */
    private Supplier<Boolean> active;

    public KeybindCategory(String id)
    {
        this.id = id;
    }

    public KeybindCategory active(Supplier<Boolean> active)
    {
        this.active = active;

        return this;
    }

    public void add(Keybind key)
    {
        this.keybinds.add(key);
    }

    public void resetKeybinds()
    {
        for (Keybind keybind : this.keybinds)
        {
            keybind.reset();
        }
    }

    public boolean isActive()
    {
        return this.active == null || this.active.get();
    }

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        for (Keybind keybind : this.keybinds)
        {
            if (keybind.isDown(key))
            {
                keybind.apply(action == GLFW.GLFW_RELEASE);

                return true;
            }
            else if (action == GLFW.GLFW_RELEASE && keybind.combo.getMainKey() == key)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void handleTextInput(int key)
    {}
}