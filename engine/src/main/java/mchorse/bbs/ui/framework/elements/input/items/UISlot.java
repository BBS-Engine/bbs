package mchorse.bbs.ui.framework.elements.input.items;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIClickable;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

public class UISlot extends UIClickable<ItemStack>
{
    public UIInventory inventory;
    public final int slot;

    private ItemStack stack = ItemStack.EMPTY;

    public boolean renderDisabled = true;
    public int lastSlot;

    public UISlot(int slot, Consumer<ItemStack> callback)
    {
        super(callback);

        this.slot = slot;
        this.inventory = new UIInventory(this);

        this.context((menu) ->
        {
            menu.action(Icons.COPY, UIKeys.ITEM_SLOT_CONTEXT_COPY, this::copyItem);

            try
            {
                ItemStack stack = ItemStack.create(Window.getClipboardMap());

                if (!stack.isEmpty())
                {
                    menu.action(Icons.PASTE, UIKeys.ITEM_SLOT_CONTEXT_PASTE, () -> this.pasteItem(stack));
                }
            }
            catch (Exception e)
            {}
        });

        this.wh(24, 24);
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public void setStack(ItemStack stack)
    {
        this.lastSlot = -1;
        this.stack = stack.copy();

        if (this.inventory.hasParent())
        {
            this.inventory.updateInventory();
        }
    }

    public void acceptStack(ItemStack stack, int slot)
    {
        this.lastSlot = slot;
        this.stack = stack.copy();

        if (this.callback != null)
        {
            this.callback.accept(stack);
        }
    }

    private void copyItem()
    {
        if (!this.stack.isEmpty())
        {
            Window.setClipboard(this.stack.toData());
        }
    }

    private void pasteItem(ItemStack stack)
    {
        this.acceptStack(stack, -1);
    }

    @Override
    protected void click(int mouseButton)
    {
        this.inventory.removeFromParent();

        UIContext context = this.getContext();

        this.inventory.relative(context.menu.overlay).xy(0.5F, 0.5F).anchor(0.5F, 0.5F);
        this.inventory.resize();
        this.inventory.updateInventory();

        context.menu.overlay.add(this.inventory);
    }

    @Override
    protected ItemStack get()
    {
        return this.stack;
    }

    @Override
    protected void renderSkin(UIContext context)
    {
        int border = this.inventory.hasParent() ? Colors.A100 | BBSSettings.primaryColor.get() : Colors.WHITE;

        context.draw.box(this.area.x, this.area.y, this.area.ex(), this.area.ey(), border);
        context.draw.box(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, Colors.LIGHTEST_GRAY);

        BBS.getItems().renderInUI(context.render, this.stack, this.area.x, this.area.y, this.area.w, this.area.h);

        if (this.area.isInside(context))
        {
            context.tooltip.set(context, this);
        }

        if (this.renderDisabled)
        {
            context.draw.lockedArea(this);
        }
    }

    @Override
    public void renderTooltip(UIContext context, Area area)
    {
        super.renderTooltip(context, area);

        UIInventory.renderItemTooltip(context, this.stack, context.mouseX, context.mouseY);
    }
}