package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.l10n.keys.IKey;

import java.util.function.Consumer;

public class UIPromptOverlayPanel extends UIMessageBarOverlayPanel
{
    public UITextbox text;

    public Consumer<String> callback;

    public UIPromptOverlayPanel(IKey title, IKey message)
    {
        this(title, message, null);
    }

    public UIPromptOverlayPanel(IKey title, IKey message, Consumer<String> callback)
    {
        super(title, message);

        this.callback = callback;
        this.text = new UITextbox(null);

        this.bar.prepend(this.text);
    }

    @Override
    protected void onAdd(UIElement parent)
    {
        super.onAdd(parent);

        this.text.textbox.moveCursorToEnd();
        parent.getContext().focus(this.text);
    }

    @Override
    public void confirm()
    {
        super.confirm();

        if (this.callback != null)
        {
            this.callback.accept(this.text.getText());
        }
    }
}
