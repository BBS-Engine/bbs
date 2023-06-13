package mchorse.bbs.ui.font;

import mchorse.bbs.graphics.text.Kerning;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;

public class UIKerning extends UIElement
{
    public UITrackpad character;
    public UITrackpad amount;

    private Kerning kerning;

    public UIKerning(Kerning kerning)
    {
        this.kerning = kerning;

        this.character = new UITrackpad((v) -> this.setCharacter(v.intValue())).limit(1).integer();
        this.amount = new UITrackpad((v) -> this.kerning.kerning = v.intValue()).integer();
        this.amount.tooltip(UIKeys.FONT_EDITOR_KERNING_AMOUNT);

        this.add(this.character, this.amount);

        this.row();

        this.setCharacter(kerning.right);
        this.amount.setValue(kerning.kerning);
    }

    public Kerning getKerning()
    {
        return this.kerning;
    }

    private void setCharacter(int character)
    {
        this.kerning.right = (char) character;

        this.character.tooltip(UIKeys.FONT_EDITOR_KERNING_CHARACTER.format(Character.getName(character).toLowerCase()));
    }
}