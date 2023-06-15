package mchorse.app.ui.inventory;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.items.ItemInventory;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.items.UIInventory;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;

public class UIPlayerInventory extends UIElement implements IUIInventory
{
    public ItemInventory inventory;
    public int perRow = 5;

    private IUIInventoryHandler handler;

    public UIPlayerInventory(ItemInventory inventory, IUIInventoryHandler handler)
    {
        super();

        this.inventory = inventory;
        this.handler = handler;

        this.wh(this.perRow * 20, (int) Math.ceil(inventory.getSize() / (float) this.perRow) * 20);
    }

    @Override
    public void accept(int slot, ItemStack stack)
    {
        this.inventory.setStack(slot, stack.copy());
    }

    @Override
    public ItemInventory getInventory()
    {
        return this.inventory;
    }

    @Override
    public boolean mouseClicked(UIContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.area.isInside(context))
        {
            int index = this.getIndex(context);

            if (index != -1)
            {
                if (this.handler.isHolding())
                {
                    this.handler.requestItem(this, index);
                }
                else
                {
                    this.handler.hold(this, index);
                }

                return true;
            }
        }

        return false;
    }

    private int getIndex(UIContext context)
    {
        int x = context.mouseX - this.area.x;
        int y = context.mouseY - this.area.y;
        int index = (x / 20) + (y / 20) * this.perRow;

        if (index >= 0 && index < this.inventory.getSize())
        {
            return index;
        }

        return -1;
    }

    @Override
    public void render(UIContext context)
    {
        context.batcher.box(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.WHITE);
        context.batcher.box(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, Colors.LIGHTEST_GRAY);

        for (int i = 0, size = this.inventory.getSize(); i < size; i++)
        {
            int x = this.area.x + 20 * (i % this.perRow);
            int y = this.area.y + 20 * (i / this.perRow);

            int diffX = context.mouseX - x;
            int diffY = context.mouseY - y;

            context.batcher.box(x + 1, y + 1, x + 19, y + 19, Colors.A25);

            ItemStack stack = this.inventory.getStack(i);
            boolean hover = diffX >= 0 && diffX < 18 && diffY >= 0 && diffY < 18;

            if (hover)
            {
                context.batcher.box(x + 1, y + 1, x + 19, y + 19, Colors.A75 | BBSSettings.primaryColor.get());
            }

            BBS.getItems().renderInUI(context.render, stack, x, y, 20, 20);
        }

        if (this.area.isInside(context))
        {
            context.tooltip.set(context, this);
        }

        super.render(context);
    }

    @Override
    public void renderTooltip(UIContext context, Area area)
    {
        super.renderTooltip(context, area);

        int index = this.getIndex(context);

        if (index != -1)
        {
            UIInventory.renderItemTooltip(context, this.inventory.getStack(index), context.mouseX, context.mouseY);
        }
    }
}