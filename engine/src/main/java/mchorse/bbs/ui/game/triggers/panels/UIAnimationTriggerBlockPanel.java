package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.AnimationTriggerBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;

public class UIAnimationTriggerBlockPanel extends UIStringTriggerBlockPanel<AnimationTriggerBlock>
{
    public UIAnimationTriggerBlockPanel(UITriggerOverlayPanel overlay, AnimationTriggerBlock block)
    {
        super(overlay, block);

        this.addDelay();
    }

    @Override
    protected ContentType getType()
    {
        return ContentType.ANIMATIONS;
    }
}