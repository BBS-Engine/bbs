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

public class UIPlayerSlot extends UIElement implements IUIInventory
{
    public int slot;
    public ItemInventory inventory;

    private IUIInventoryHandler handler;

    public UIPlayerSlot(int slot, ItemInventory inventory, IUIInventoryHandler handler)
    {
        this.slot = slot;
        this.inventory = inventory;
        this.handler = handler;

        this.wh(20, 20);
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
    protected boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context))
        {
            if (this.handler.isHolding())
            {
                this.handler.requestItem(this, this.slot);
            }
            else
            {
                this.handler.hold(this, this.slot);
            }

            return true;
        }

        return false;
    }

    @Override
    public void render(UIContext context)
    {
        context.batcher.box(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.WHITE);
        context.batcher.box(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, Colors.LIGHTEST_GRAY);

        if (this.area.isInside(context))
        {
            context.batcher.box(this.area.x + 1, this.area.y + 1, this.area.x + 19, this.area.y + 19, Colors.A75 | BBSSettings.primaryColor.get());
        }

        BBS.getItems().renderInUI(context.render, this.inventory.getStack(this.slot), this.area.x, this.area.y, this.area.w, this.area.h);

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

        UIInventory.renderItemTooltip(context, this.inventory.getStack(this.slot), context.mouseX, context.mouseY);
    }
}