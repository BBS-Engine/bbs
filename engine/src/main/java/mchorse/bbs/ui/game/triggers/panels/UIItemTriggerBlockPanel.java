package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.ItemTriggerBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.game.utils.UITarget;
import mchorse.bbs.ui.utils.UI;

public class UIItemTriggerBlockPanel extends UITriggerBlockPanel<ItemTriggerBlock>
{
    public UITarget target;
    public UICirculate mode;
    public UISlot slot;

    public UIItemTriggerBlockPanel(UITriggerOverlayPanel overlay, ItemTriggerBlock block)
    {
        super(overlay, block);

        this.target = new UITarget(null).skipGlobal();
        this.mode = new UICirculate(this::toggleItemCheck);

        for (ItemTriggerBlock.ItemMode mode : ItemTriggerBlock.ItemMode.values())
        {
            this.mode.addLabel(UIKeys.C_ITEM_TRIGGER.get(mode));
        }

        this.slot = new UISlot(0, (stack) -> this.block.stack = stack.copy());
        this.slot.marginTop(-2).marginBottom(-2);

        this.target.setTarget(block.target);
        this.mode.setValue(block.mode.ordinal());
        this.slot.setStack(block.stack);

        this.add(UI.row(this.slot, this.mode));
        this.add(this.target.marginTop(12));
    }

    private void toggleItemCheck(UICirculate b)
    {
        this.block.mode = ItemTriggerBlock.ItemMode.values()[b.getValue()];
    }
}