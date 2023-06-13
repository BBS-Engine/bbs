package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.DialogueTriggerBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;

public class UIDialogueTriggerBlockPanel extends UIDataTriggerBlockPanel<DialogueTriggerBlock>
{
    public UIDialogueTriggerBlockPanel(UITriggerOverlayPanel overlay, DialogueTriggerBlock block)
    {
        super(overlay, block);

        this.addDelay();
    }

    @Override
    protected ContentType getType()
    {
        return ContentType.DIALOGUES;
    }
}