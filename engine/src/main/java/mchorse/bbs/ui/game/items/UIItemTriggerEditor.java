package mchorse.bbs.ui.game.items;

import mchorse.bbs.game.items.ItemTrigger;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.utils.UI;

public class UIItemTriggerEditor extends UIItemEditor<ItemTrigger>
{
    public UITrigger useTrigger;

    public UIItemTriggerEditor(UIItemsPanel panel, ItemTrigger item)
    {
        super(panel, item);

        this.useTrigger = new UITrigger(item.useTrigger);
        this.useTrigger.onClose(panel::dirty);

        this.add(UI.label(UIKeys.ITEM_PANEL_EDITORS_TRIGGER_USE_TRIGGER), this.useTrigger);
    }
}