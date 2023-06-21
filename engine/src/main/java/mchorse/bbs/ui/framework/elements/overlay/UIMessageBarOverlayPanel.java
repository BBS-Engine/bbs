package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.UI;

public abstract class UIMessageBarOverlayPanel extends UIMessageOverlayPanel
{
    public UIButton confirm;
    public UIElement bar;

    public UIMessageBarOverlayPanel(IKey title, IKey message)
    {
        super(title, message);

        this.confirm = new UIButton(UIKeys.OK, (b) -> this.confirm());
        this.bar = UI.row(this.confirm);

        this.confirm.w(80);
        this.bar.relative(this.content).x(6).y(1F, -6).w(1F, -12).anchor(0, 1F);

        this.content.add(this.bar);
    }

    @Override
    public void confirm()
    {
        this.close();
    }
}