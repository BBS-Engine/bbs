package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.StringTriggerBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.l10n.keys.IKey;

public abstract class UIStringTriggerBlockPanel <T extends StringTriggerBlock> extends UITriggerBlockPanel<T>
{
    public UIButton picker;

    public UIStringTriggerBlockPanel(UITriggerOverlayPanel overlay, T block)
    {
        super(overlay, block);

        this.picker = new UIButton(this.getLabel(), (b) -> this.openOverlay());
        this.add(this.picker);
    }

    protected IKey getLabel()
    {
        return this.getType().getPickLabel();
    }

    protected abstract ContentType getType();

    protected void openOverlay()
    {
        UIDataUtils.openPicker(this.getContext(), this.getType(), this.block.id, this::setString);
    }

    private void setString(String string)
    {
        this.block.id = string;
    }
}