package mchorse.bbs.core.keybinds;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.utils.keys.KeyCombo;

import java.util.function.Consumer;

/**
 * Keybind class
 * 
 * This bad boy stores information about the keybind and has utility 
 * methods for triggering a callback and checking whether this keybind 
 * is down.
 */
public class Keybind
{
    /**
     * Identifier of this keybind
     */
    public final String id;

    /**
     * Array of key codes which should be met in order for this keybind 
     * to be considered down
     */
    public KeyCombo combo;

    /**
     * Callback which should be called when this keybind's keys was pressed
     */
    public Runnable onKeyPressed;

    /**
     * Callback which should be called when this keybind's keys was released
     */
    public Runnable onKeyReleased;

    /**
     * How many times this keybind was pressed (GLFW seems to send 
     * multiple press events when the key is held so this allows)
     */
    private int counter = -1;

    public Keybind(String id)
    {
        this(id, (Runnable) null);
    }

    public Keybind(String id, Consumer<Boolean> callback)
    {
        this.id = id;
        this.onKeyReleased = callback == null ? null : () -> callback.accept(true);
        this.onKeyPressed = callback == null ? null :() -> callback.accept(false);
    }

    public Keybind(String id, Runnable onKeyReleased)
    {
        this.id = id;
        this.onKeyReleased = onKeyReleased;
    }

    public Keybind onPress(Runnable callback)
    {
        this.onKeyPressed = callback;

        return this;
    }

    /**
     * Set keycodes 
     */
    public Keybind keys(int... keyCodes)
    {
        this.combo = new KeyCombo(IKey.raw(this.id), keyCodes);

        return this;
    }

    /**
     * Check whether this keybind is down. Useful to check somewhere in 
     * the loop 
     */
    public boolean isDown()
    {
        return this.counter >= 0;
    }

    /**
     * Check whether this key bind is down, but also supplying the key 
     * code that was currently pressed or released 
     */
    public boolean isDown(int inKey)
    {
        boolean result = true;
        boolean has = false;

        for (int keyCode : this.combo.keys)
        {
            if (inKey != keyCode) result = result && Window.isKeyPressed(keyCode);
            else has = true;
        }

        if (!has && inKey != -1) return false;
        if (result && inKey != -1) this.counter++;

        return result;
    }

    /**
     * Trigger the callback was given to this keycode 
     */
    public void apply(boolean release)
    {
        if (!release && this.counter > 0)
        {
            return;
        }

        if (release)
        {
            this.counter = -1;
        }

        if (release && this.onKeyReleased != null)
        {
            this.onKeyReleased.run();
        }
        else if (!release && this.onKeyPressed != null)
        {
            this.onKeyPressed.run();
        }
    }

    public void reset()
    {
        this.counter = -1;
    }
}