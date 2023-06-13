package mchorse.bbs.ui.framework.elements.input.text;

import mchorse.bbs.BBS;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.IFocusedUIElement;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.utils.Textbox;

public abstract class UIBaseTextbox extends UIElement implements IFocusedUIElement
{
    public Textbox textbox;

    public UIBaseTextbox()
    {
        super();

        this.textbox = new Textbox(this::userInput);
        this.textbox.setFont(BBS.getRender().getFont());
    }

    protected void userInput(String string)
    {}

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        this.textbox.setEnabled(enabled);
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        this.textbox.setVisible(visible);
    }

    @Override
    public boolean isFocused()
    {
        return this.textbox.isFocused();
    }

    @Override
    public void focus(UIContext context)
    {
        this.textbox.setFocused(true);
    }

    @Override
    public void unfocus(UIContext context)
    {
        this.textbox.setFocused(false);
    }

    @Override
    public void selectAll(UIContext context)
    {
        this.textbox.moveCursorToStart();
        this.textbox.setSelection(this.textbox.getText().length());
    }

    @Override
    public void unselect(UIContext context)
    {
        this.textbox.deselect();
    }

    public String getText()
    {
        return this.textbox.getText();
    }
}