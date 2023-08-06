package mchorse.bbs.core.keybinds;

import mchorse.bbs.core.input.IKeyHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Keybind manager
 * 
 * This bad boy is responsible for handling and registering keybind 
 * categories 
 */
public class KeybindManager implements IKeyHandler
{
    private List<KeybindCategory> categories = new ArrayList<>();

    public void add(KeybindCategory category)
    {
        this.categories.add(category);
    }

    public void remove(KeybindCategory category)
    {
        this.categories.remove(category);
    }

    public void resetKeybinds()
    {
        for (KeybindCategory category : this.categories)
        {
            category.resetKeybinds();
        }
    }

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        boolean result = false;

        for (KeybindCategory category : this.categories)
        {
            if (category.isActive() && category.handleKey(key, scancode, action, mods))
            {
                result = true;
            }
        }

        return result;
    }

    @Override
    public void handleTextInput(int key)
    {}
}