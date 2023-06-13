package mchorse.bbs.ui.framework.elements.input;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.keys.KeyAction;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.utils.colors.Colors;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class UIKeybind extends UIElement
{
    public KeyCombo combo;
    public boolean reading;
    public Consumer<KeyCombo> callback;

    public UIKeybind(Consumer<KeyCombo> callback)
    {
        super();

        this.combo = new KeyCombo(null, 0);
        this.combo.keys.clear();

        this.callback = callback;
        this.h(20);
    }

    public void setKeyCodes(int... keys)
    {
        this.combo.keys.clear();

        for (int i : keys)
        {
            this.combo.keys.add(i);
        }
    }

    public void setKeyCombo(KeyCombo combo)
    {
        this.combo.copy(combo);
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && context.mouseButton == 0)
        {
            context.unfocus();

            this.reading = true;
            this.combo.keys.clear();
        }

        return this.area.isInside(context);
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (this.reading)
        {
            if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
            {
                this.combo.keys.clear();
                this.reading = false;

                this.callback();

                return true;
            }

            if (context.getKeyAction() == KeyAction.PRESSED)
            {
                int key = context.getKeyCode();

                if (!this.combo.keys.contains(key))
                {
                    this.combo.keys.add(0, key);
                }
            }

            if (this.combo.keys.isEmpty())
            {
                return false;
            }

            for (int key : this.combo.keys)
            {
                if (Window.isKeyPressed(key))
                {
                    return true;
                }
            }

            this.reading = false;

            this.callback();

            return true;
        }

        return super.subKeyPressed(context);
    }

    private void callback()
    {
        if (this.callback != null)
        {
            this.callback.accept(this.combo);
        }
    }

    @Override
    public void render(UIContext context)
    {
        String label = this.combo.keys.isEmpty() ? UIKeys.NONE.get() : this.combo.getKeyCombo();
        int w = context.font.getWidth(label) - 1;

        if (this.reading)
        {
            this.area.render(context.draw, Colors.A100 | BBSSettings.primaryColor.get());

            int x = this.area.mx(w);
            int y = this.area.my() + context.font.getHeight() - 1;
            float a = (float) Math.sin(context.getTickTransition() / 2D);
            int c = Colors.setA(Colors.WHITE, a * 0.5F + 0.5F);

            context.draw.box(x, y, x + w, y + 1, c);
        }
        else
        {
            this.area.render(context.draw, Colors.A100);
        }

        context.font.renderWithShadow(context.render, label, this.area.mx(w), this.area.my() - context.font.getHeight() / 2);

        super.render(context);
    }
}