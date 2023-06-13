package mchorse.bbs.ui.framework.elements.input.text;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.ITextColoring;
import mchorse.bbs.utils.Patterns;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * GUI text element
 * 
 * This element is a wrapper for the text field class
 */
public class UITextbox extends UIBaseTextbox implements ITextColoring
{
    public static final Predicate<String> FILENAME_PREDICATE = (s) -> Patterns.FILENAME.matcher(s).find();

    public Consumer<String> callback;

    private boolean delayedInput;

    public UITextbox()
    {
        this(null);
    }

    public UITextbox(Consumer<String> callback)
    {
        super();

        this.callback = callback;

        this.border().h(20);
    }

    public UITextbox(int maxLength, Consumer<String> callback)
    {
        this(callback);

        this.textbox.setLength(maxLength);
    }

    public UITextbox filename()
    {
        return this.validator(FILENAME_PREDICATE);
    }

    public UITextbox validator(Predicate<String> validator)
    {
        this.textbox.setValidator(validator);

        return this;
    }

    public UITextbox background(boolean background)
    {
        this.textbox.setBackground(background);
        this.resize();

        return this;
    }

    public UITextbox placeholder(IKey placeholder)
    {
        this.textbox.setPlaceholder(placeholder);

        return this;
    }

    public UITextbox border()
    {
        this.textbox.setBorder(true);

        return this;
    }

    public UITextbox noBorder()
    {
        this.textbox.setBorder(false);

        return this;
    }

    public UITextbox delayedInput()
    {
        this.delayedInput = true;

        return this;
    }

    public void setText(String text)
    {
        if (text == null)
        {
            text = "";
        }

        this.textbox.setText(text);
        this.textbox.moveCursorToStart();
    }

    @Override
    protected void userInput(String string)
    {
        if (this.callback != null && !this.delayedInput)
        {
            this.callback.accept(string);
        }
    }

    @Override
    public void unfocus(UIContext context)
    {
        super.unfocus(context);

        if (this.callback != null && this.delayedInput)
        {
            this.callback.accept(this.textbox.getText());
        }
    }

    @Override
    public void setColor(int color, boolean shadow)
    {
        this.textbox.setColor(color);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.textbox.area.copy(this.area);

        if (!this.textbox.hasBackground())
        {
            int h = this.textbox.getFont().getHeight();

            this.textbox.area.x += 4;
            this.textbox.area.y += ((this.area.h - h) / 2);
            this.textbox.area.w -= 8;
            this.textbox.area.h = h;
        }
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        boolean wasFocused = this.textbox.isFocused();

        this.textbox.mouseClicked(context.mouseX, context.mouseY, context.mouseButton);

        if (wasFocused != this.textbox.isFocused())
        {
            context.focus(wasFocused ? null : this);
        }

        return context.mouseButton == 0 && this.area.isInside(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.textbox.mouseReleased(context.mouseX, context.mouseY, context.mouseButton);

        return super.subMouseReleased(context);
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (this.isFocused())
        {
            if (context.isPressed(GLFW.GLFW_KEY_TAB))
            {
                context.focus(this, Window.isShiftPressed() ? -1 : 1);

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
            {
                context.unfocus();

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_ENTER) && this.delayedInput)
            {
                if (this.callback != null)
                {
                    this.callback.accept(this.textbox.getText());
                }

                return true;
            }
        }

        return this.textbox.keyPressed(context);
    }

    @Override
    protected boolean subTextInput(UIContext context)
    {
        return this.isFocused() && this.textbox.textInput(context.getInputCharacter());
    }

    @Override
    public void render(UIContext context)
    {
        this.textbox.render(context);

        context.draw.lockedArea(this);

        super.render(context);
    }
}