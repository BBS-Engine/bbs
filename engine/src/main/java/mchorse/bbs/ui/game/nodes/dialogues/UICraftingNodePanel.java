package mchorse.bbs.ui.game.nodes.dialogues;

import mchorse.bbs.game.dialogues.nodes.CraftingNode;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;

public class UICraftingNodePanel extends UIEventBaseNodePanel<CraftingNode>
{
    public UIButton crafting;

    public UICraftingNodePanel()
    {
        super();

        this.crafting = new UIButton(UIKeys.OVERLAYS_CRAFTING, (b) -> this.openCraftingTables());

        this.add(this.crafting);
    }

    private void openCraftingTables()
    {
        UIDataUtils.openPicker(this.getContext(), ContentType.CRAFTING_TABLES, this.node.table, (name) -> this.node.table = name);
    }
}