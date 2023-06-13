package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.game.conditions.blocks.StateConditionBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;
import mchorse.bbs.ui.game.utils.UIComparison;
import mchorse.bbs.ui.game.utils.UITarget;
import mchorse.bbs.ui.utils.UI;

public class UIStateConditionBlockPanel extends UIConditionBlockPanel<StateConditionBlock>
{
    public UITextbox id;
    public UIComparison comparison;
    public UITarget target;

    public UIStateConditionBlockPanel(UIConditionOverlayPanel overlay, StateConditionBlock block)
    {
        super(overlay, block);

        this.id = new UITextbox(1000, (t) -> this.block.id = t);
        this.id.setText(block.id);
        this.comparison = new UIComparison(block.comparison);
        this.target= new UITarget(block.target);

        this.add(UI.label(UIKeys.CONDITIONS_STATE_ID).marginTop(12), this.id);
        this.add(this.target.marginTop(12));
        this.add(this.comparison.marginTop(12));
    }
}