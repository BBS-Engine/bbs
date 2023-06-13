package mchorse.bbs.ui.utils.keys;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Keybind class
 */
public class Keybind
{
    private IKey label;
    private IKey category;

    private KeyCombo combo;
    public Runnable callback;
    public boolean inside;
    public Supplier<Boolean> active;

    public Keybind(KeyCombo combo, Runnable callback)
    {
        this.combo = combo;
        this.callback = callback;
    }

    public Keybind inside()
    {
        this.inside = true;

        return this;
    }

    public Keybind active(Supplier<Boolean> active)
    {
        this.active = active;

        return this;
    }

    public Keybind label(IKey label)
    {
        this.label = label;

        return this;
    }

    public Keybind category(IKey category)
    {
        this.category = category;

        return this;
    }

    public int getScore()
    {
        return this.combo.keys.size();
    }

    public IKey getLabel()
    {
        return this.label == null ? this.combo.label : this.label;
    }

    public IKey getCategory()
    {
        return this.category == null ? this.combo.category : this.category;
    }

    public String getKeyCombo()
    {
        return this.combo.getKeyCombo();
    }

    public boolean check(int keyCode, KeyAction keyAction, boolean inside)
    {
        if (keyAction == KeyAction.REPEAT && !this.combo.repeatable)
        {
            return false;
        }

        if (keyCode != this.combo.getMainKey())
        {
            return false;
        }

        for (int i = 1; i < this.combo.keys.size(); i++)
        {
            if (!this.isKeyDown(this.combo.keys.get(i)))
            {
                return false;
            }
        }

        if (this.inside)
        {
            return inside;
        }

        return true;
    }

    protected boolean isKeyDown(int key)
    {
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)
        {
            return Window.isShiftPressed();
        }
        else if (key == GLFW.GLFW_KEY_LEFT_CONTROL || key == GLFW.GLFW_KEY_RIGHT_CONTROL)
        {
            return Window.isCtrlPressed();
        }
        else if (key == GLFW.GLFW_KEY_LEFT_ALT || key == GLFW.GLFW_KEY_RIGHT_ALT)
        {
            return Window.isAltPressed();
        }

        return Window.isKeyPressed(key);
    }

    public boolean isActive()
    {
        return this.active == null || this.active.get();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Keybind)
        {
            Keybind keybind = (Keybind) obj;

            return Objects.equals(this.combo.keys, keybind.combo.keys) && this.inside == keybind.inside;
        }

        return super.equals(obj);
    }
}