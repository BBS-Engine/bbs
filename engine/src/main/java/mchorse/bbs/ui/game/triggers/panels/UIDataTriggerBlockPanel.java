package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.DataTriggerBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;

public abstract class UIDataTriggerBlockPanel <T extends DataTriggerBlock> extends UIStringTriggerBlockPanel<T>
{
    public UITextbox data;

    public UIDataTriggerBlockPanel(UITriggerOverlayPanel overlay, T block)
    {
        super(overlay, block);

        this.data = new UITextbox(100000, (text) -> this.block.customData = text);
        this.data.tooltip(UIKeys.NODES_EVENT_DATA_TOOLTIP);
        this.data.setText(block.customData);
        this.add(UI.label(UIKeys.NODES_EVENT_DATA).marginTop(12), this.data);
    }
}