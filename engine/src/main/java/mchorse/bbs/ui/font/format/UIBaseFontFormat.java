package mchorse.bbs.ui.font.format;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.text.format.BaseFontFormat;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UILabel;

public class UIBaseFontFormat <T extends BaseFontFormat> extends UIElement
{
    public UILabel name;
    public UITextbox control;

    protected T format;

    public UIBaseFontFormat()
    {
        super();

        this.name = new UILabel(IKey.EMPTY).anchor(0F, 0.5F);
        this.control = new UITextbox(1, (t) -> this.format.setControlCharacter(t));

        this.name.w(80);
        this.row();

        this.add(this.name, this.control);
    }

    public void fill(T format)
    {
        this.format = format;

        this.name.label = UIKeys.C_FONT_FORMAT.get(BBS.getFactoryFontFormats().getType(format).toString());
        this.control.setText(String.valueOf(format.getControlCharacter()));
    }
}