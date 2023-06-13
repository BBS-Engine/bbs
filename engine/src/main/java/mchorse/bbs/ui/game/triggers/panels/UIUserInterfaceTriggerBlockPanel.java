package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.scripts.ui.UserInterfaceTriggerBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;

public class UIUserInterfaceTriggerBlockPanel extends UIStringTriggerBlockPanel<UserInterfaceTriggerBlock>
{
    public UIUserInterfaceTriggerBlockPanel(UITriggerOverlayPanel overlay, UserInterfaceTriggerBlock block)
    {
        super(overlay, block);

        this.addDelay();
    }

    @Override
    protected ContentType getType()
    {
        return ContentType.UIS;
    }
}