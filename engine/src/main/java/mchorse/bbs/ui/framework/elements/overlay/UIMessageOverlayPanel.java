package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.l10n.keys.IKey;

public class UIMessageOverlayPanel extends UIOverlayPanel
{
    public UIText message;

    public UIMessageOverlayPanel(IKey title, IKey message)
    {
        super(title);

        this.message = new UIText().text(message).anchorX(0.5F);
        this.message.relative(this.content).x(0.5F).y(12).w(0.7F).anchorX(0.5F);

        this.content.add(this.message);
    }

    public void setMessage(IKey message)
    {
        this.message.text(message);
    }
}