package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.game.conditions.blocks.ItemConditionBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;
import mchorse.bbs.ui.game.utils.UITarget;
import mchorse.bbs.ui.utils.UI;

public class UIItemConditionBlockPanel extends UIConditionBlockPanel<ItemConditionBlock>
{
    public UITarget target;
    public UICirculate check;
    public UISlot slot;

    public UIItemConditionBlockPanel(UIConditionOverlayPanel overlay, ItemConditionBlock block)
    {
        super(overlay, block);

        this.target = new UITarget(block.target).skipGlobal();
        this.check = new UICirculate(this::toggleItemCheck);

        for (ItemConditionBlock.ItemCheck check : ItemConditionBlock.ItemCheck.values())
        {
            this.check.addLabel(UIKeys.C_ITEM_CONDITION.get(check));
        }

        this.check.setValue(block.check.ordinal());
        this.slot = new UISlot(0, (stack) -> this.block.stack = stack.copy());
        this.slot.marginTop(-2).marginBottom(-2);
        this.slot.setStack(block.stack);

        this.add(UI.row(this.slot, this.check));
        this.add(this.target.marginTop(12));
    }

    private void toggleItemCheck(UICirculate b)
    {
        this.block.check = ItemConditionBlock.ItemCheck.values()[b.getValue()];
    }
}