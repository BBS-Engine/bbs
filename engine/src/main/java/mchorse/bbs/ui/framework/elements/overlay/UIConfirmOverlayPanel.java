package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;

import java.util.function.Consumer;

public class UIConfirmOverlayPanel extends UIMessageOverlayPanel
{
    public UIButton confirm;

    public Consumer<Boolean> callback;

    private boolean result;

    public UIConfirmOverlayPanel(IKey title, IKey message, Consumer<Boolean> callback)
    {
        super(title, message);

        this.callback = callback;

        this.confirm = new UIButton(UIKeys.GENERAL_OK, (b) -> this.confirm());

        this.confirm.relative(this.content).x(0.5F).y(1F, -10).w(80).anchor(0.5F, 1F);
        this.content.add(this.confirm);
    }

    @Override
    public void confirm()
    {
        this.result = true;

        this.close();
    }

    @Override
    public void onClose()
    {
        super.onClose();

        if (this.callback != null)
        {
            this.callback.accept(this.result);
        }
    }
}