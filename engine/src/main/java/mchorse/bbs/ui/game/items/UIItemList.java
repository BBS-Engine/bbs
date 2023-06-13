package mchorse.bbs.ui.game.items;

import mchorse.bbs.BBS;
import mchorse.bbs.game.items.ItemEntry;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class UIItemList extends UIList<ItemEntry>
{
    public UIItemList(Consumer<List<ItemEntry>> callback)
    {
        super(callback);
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort(Comparator.comparing((a) -> a.item.getId().toString()));

        return true;
    }

    @Override
    protected void renderElementPart(UIContext context, ItemEntry element, int i, int x, int y, boolean hover, boolean selected)
    {
        super.renderElementPart(context, element, i, x, y, hover, selected);

        BBS.getItems().renderInUI(context.render, new ItemStack(element), x + this.area.w - 20, y, 20, 20);
    }

    @Override
    protected String elementToString(int i, ItemEntry element)
    {
        String s = element.item.getId().toString();
        Link type = BBS.getFactoryItems().getTypeSilent(element.item);

        if (type != null)
        {
            s += " (" + type + ")";
        }

        return s;
    }
}