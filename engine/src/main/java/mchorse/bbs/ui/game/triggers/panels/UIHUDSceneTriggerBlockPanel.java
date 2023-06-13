package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.HUDSceneTriggerBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;

public class UIHUDSceneTriggerBlockPanel extends UIStringTriggerBlockPanel<HUDSceneTriggerBlock>
{
    public UICirculate mode;

    public UIHUDSceneTriggerBlockPanel(UITriggerOverlayPanel overlay, HUDSceneTriggerBlock block)
    {
        super(overlay, block);

        this.mode = new UICirculate(this::toggleItemCheck);

        for (HUDSceneTriggerBlock.HUDMode mode : HUDSceneTriggerBlock.HUDMode.values())
        {
            this.mode.addLabel(UIKeys.C_HUD_MODE.get(mode));
        }

        this.mode.setValue(block.mode.ordinal());

        this.add(this.mode);
        this.addDelay();
    }

    private void toggleItemCheck(UICirculate b)
    {
        this.block.mode = HUDSceneTriggerBlock.HUDMode.values()[b.getValue()];
    }

    @Override
    protected ContentType getType()
    {
        return ContentType.HUDS;
    }
}