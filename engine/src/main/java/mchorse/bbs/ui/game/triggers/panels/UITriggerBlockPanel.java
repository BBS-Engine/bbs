package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.BBS;
import mchorse.bbs.game.triggers.blocks.TriggerBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.utils.UI;

public abstract class UITriggerBlockPanel <T extends TriggerBlock> extends UIElement
{
    public UITrackpad frequency;

    protected UITriggerOverlayPanel overlay;
    protected T block;

    public UITriggerBlockPanel(UITriggerOverlayPanel overlay, T block)
    {
        super();

        this.overlay = overlay;
        this.block = block;

        this.frequency = new UITrackpad((v) -> this.block.frequency = v.intValue());
        this.frequency.limit(1).integer().setValue(block.frequency);

        UILabel label = UI.label(UIKeys.C_TRIGGER.get(BBS.getFactoryTriggers().getType(block)));

        this.column().vertical().stretch();
        this.add(label);
    }

    public void addDelay()
    {
        this.add(UI.label(UIKeys.TRIGGERS_FREQUENCY).marginTop(12), this.frequency);
    }
}