package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.l10n.keys.IKey;

public class UITextboxOverlayPanel extends UIOverlayPanel
{
    public UITextbox text;

    public UITextboxOverlayPanel(IKey title, UITextbox element)
    {
        super(title);

        this.text = new UITextbox(element.textbox.getLength(), (t) ->
        {
            element.setText(t);

            if (element.callback != null)
            {
                element.callback.accept(t);
            }
        });
        this.text.setText(element.getText());

        this.text.relative(this.content).y(1F, -30).w(1F);
        this.content.add(this.text);
    }
}