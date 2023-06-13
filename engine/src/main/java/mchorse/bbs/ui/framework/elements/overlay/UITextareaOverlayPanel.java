package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextarea;
import mchorse.bbs.l10n.keys.IKey;

import java.util.function.Consumer;

public class UITextareaOverlayPanel extends UIMessageBarOverlayPanel
{
    public UITextarea text;

    public Consumer<String> callback;

    public UITextareaOverlayPanel(IKey title, IKey message)
    {
        this(title, message, null);
    }

    public UITextareaOverlayPanel(IKey title, IKey message, Consumer<String> callback)
    {
        super(title, message);

        this.callback = callback;
        this.text = new UITextarea(null).background();
        this.text.relative(this.message).w(1F).y(1F, 5).hTo(this.bar.area, -5);

        this.content.add(this.text);
    }

    @Override
    protected void onAdd(UIElement parent)
    {
        super.onAdd(parent);

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