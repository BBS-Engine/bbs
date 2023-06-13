package mchorse.bbs.ui.game.utils;

import mchorse.bbs.BBS;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.List;

public class UIItems extends UIElement
{
    public UIElement stacks;

    private List<ItemStack> items;

    public UIItems(IKey title, List<ItemStack> items)
    {
        super();

        FontRenderer font = BBS.getRender().getFont();
        UILabel label = UI.label(title);
        UIIcon add = new UIIcon(Icons.ADD, (b) ->
        {
            this.items.add(ItemStack.EMPTY);
            this.addItem(ItemStack.EMPTY);
            this.getParentContainer().resize();
        });
        add.wh(10, 8);

        UIElement row = UI.row(5, 0, font.getHeight(), label, add);
        this.stacks = new UIElement();

        label.h(0);
        row.row().preferred(0);
        this.stacks.grid(5).width(24).resizes(true);

        this.column().vertical().stretch();
        this.add(row, this.stacks);

        this.set(items);
    }

    public void set(List<ItemStack> items)
    {
        this.stacks.removeAll();

        this.items = items;

        if (this.items != null)
        {
            for (ItemStack stack : this.items)
            {
                this.addItem(stack);
            }
        }
    }

    public void addItem(ItemStack stack)
    {
        UISlot slot = new UISlot(0, null);

        slot.callback = (item) ->
        {
            int index = this.stacks.getChildren().indexOf(slot);

            if (index != -1)
            {
                this.items.set(index, item.copy());
            }
        };
        slot.setStack(stack);
        slot.context((menu) -> menu.action(Icons.REMOVE, UIKeys.ITEMS_CONTEXT_REMOVE, Colors.NEGATIVE, () ->
        {
            int index = this.stacks.getChildren().indexOf(slot);

            if (index != -1)
            {
                this.items.remove(index);
                slot.removeFromParent();
                this.getParentContainer().resize();
            }
        }));

        this.stacks.add(slot);
    }
}
