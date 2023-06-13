package mchorse.app.ui.inventory;

import mchorse.bbs.BBS;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;

public class UIInventoryHandler extends UIElement implements IUIInventoryHandler
{
    private IUIInventory currentInventory;
    private int currentIndex = -1;
    private ItemStack currentStack = ItemStack.EMPTY;

    @Override
    public void hold(IUIInventory element, int index)
    {
        if (element == null)
        {
            this.currentStack = ItemStack.EMPTY;
            this.currentInventory = element;
            this.currentIndex = index;
        }
        else
        {
            this.currentStack = element.getInventory().getStack(index);

            if (!this.currentStack.isEmpty())
            {
                element.getInventory().setStack(index, ItemStack.EMPTY);

                this.currentInventory = element;
                this.currentIndex = index;
            }
        }
    }

    @Override
    public IUIInventory getCurrentHolder()
    {
        return this.currentInventory;
    }

    @Override
    public int getCurrentIndex()
    {
        return this.currentIndex;
    }

    @Override
    public void requestItem(IUIInventory inventory, int index)
    {
        if (this.currentInventory != null)
        {
            ItemStack stack = inventory.getInventory().getStack(index);
            ItemStack current = this.currentStack;

            if (stack.isEmpty())
            {
                this.hold(null, -1);

                inventory.accept(index, current);
            }
            else
            {
                if (ItemStack.equal(stack, current))
                {
                    int total = stack.getSize() + current.getSize();
                    int max = stack.getMaxSize();

                    if (total <= max)
                    {
                        current.setSize(total);

                        this.hold(null, -1);
                    }
                    else
                    {
                        current.setSize(max);
                        stack.setSize(total - max);

                        this.hold(inventory, index);
                    }
                }
                else
                {
                    this.hold(inventory, index);
                }

                inventory.accept(index, current);
            }
        }
    }

    public void close()
    {
        if (this.currentInventory != null)
        {
            this.currentInventory.accept(this.currentIndex, this.currentStack);
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.currentInventory != null)
        {
            BBS.getItems().renderInUI(context.render, this.currentStack, context.mouseX - 10, context.mouseY - 10, 20, 20);
        }
    }
}