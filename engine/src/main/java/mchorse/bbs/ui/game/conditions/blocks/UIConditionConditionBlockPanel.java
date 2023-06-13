package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.game.conditions.blocks.ConditionConditionBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;

public class UIConditionConditionBlockPanel extends UIConditionBlockPanel<ConditionConditionBlock>
{
    public UIButton condition;

    public UIConditionConditionBlockPanel(UIConditionOverlayPanel overlay, ConditionConditionBlock block)
    {
        super(overlay, block);

        this.condition = new UIButton(UIKeys.CONDITIONS_CONDITION_EDIT, this::openConditionEditor);

        this.add(this.condition);
    }

    private void openConditionEditor(UIButton b)
    {
        UIConditionOverlayPanel panel = new UIConditionOverlayPanel(this.block.condition);

        UIOverlay.addOverlay(this.getContext(), panel, 0.55F, 0.75F);
    }
}