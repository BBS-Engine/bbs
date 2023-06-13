package mchorse.bbs.ui.game.items;

import mchorse.bbs.BBS;
import mchorse.bbs.game.items.Item;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.UI;

public class UIItemEditor <T extends Item> extends UIElement
{
    protected T item;
    protected UIItemsPanel panel;

    public UIItemEditor(UIItemsPanel panel, T item)
    {
        this.panel = panel;
        this.item = item;

        this.column().vertical().stretch();

        this.add(UI.label(UIKeys.C_ITEM.get(BBS.getFactoryItems().getType(item))).background().marginBottom(8));
    }
}