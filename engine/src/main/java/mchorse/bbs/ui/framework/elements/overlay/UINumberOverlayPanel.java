package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.l10n.keys.IKey;

import java.util.function.Consumer;

public class UINumberOverlayPanel extends UIMessageBarOverlayPanel
{
    public UITrackpad value;

    public Consumer<Double> callback;

    public UINumberOverlayPanel(IKey title, IKey message, Consumer<Double> callback)
    {
        super(title, message);

        this.callback = callback;
        this.value = new UITrackpad();

        this.bar.prepend(this.value);
    }

    @Override
    protected void onAdd(UIElement parent)
    {
        super.onAdd(parent);

        parent.getContext().focus(this.value);
    }

    @Override
    public void confirm()
    {
        super.confirm();

        if (this.callback != null)
        {
            this.callback.accept(this.value.getValue());
        }
    }
}
