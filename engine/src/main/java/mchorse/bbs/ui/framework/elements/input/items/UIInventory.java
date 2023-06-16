package mchorse.bbs.ui.framework.elements.input.items;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.items.Item;
import mchorse.bbs.game.items.ItemEntry;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.EventPropagation;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.ScrollArea;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIInventory extends UIElement
{
    public static final int TILE_SIZE = 20;

    private static List<ItemStack> container = new ArrayList<ItemStack>();

    public UITrackpad count;
    public UITextbox search;

    public UISlot slot;
    protected ScrollArea inventory = new ScrollArea(TILE_SIZE);

    private ItemStack active = ItemStack.EMPTY;

    /**
     * Draw item tooltip
     */
    public static void renderItemTooltip(UIContext context, ItemStack stack, int x, int y)
    {
        if (stack.isEmpty())
        {
            return;
        }

        x += 8;
        y -= 8;

        int w = 180;
        int h = 30;
        String description = stack.getDescription();
        List<String> lines = description.isEmpty() ? Collections.emptyList() : context.font.split(description, w - 14);

        if (!lines.isEmpty())
        {
            h += lines.size() * (context.font.getHeight() + 4) - 4 + 7;
        }

        x = MathUtils.clamp(x, 2, context.menu.width - w - 2);
        y = MathUtils.clamp(y, 2, context.menu.height - h - 2);

        context.batcher.box(x, y, x + w, y + h, Colors.A75);
        context.batcher.outline(x + 2, y + 2, x + w - 2, y + h - 2, Colors.A100 | stack.getFrameColor());

        BBS.getItems().renderInUI(context.render, stack, x + 5, y + 5, 20, 20);
        context.batcher.textShadow(stack.getDisplayName(), x + 30, y + 5 + (20 - context.font.getHeight()) / 2);

        int nx = x + 7;
        int ny = y + 30;

        for (String line : lines)
        {
            context.batcher.textShadow(line, nx, ny);

            ny += context.font.getHeight() + 4;
        }
    }

    public UIInventory(UISlot slot)
    {
        super();

        this.count = new UITrackpad((v) -> this.setCount(v.intValue()));
        this.count.limit(1).integer();
        this.search = new UITextbox((t) -> this.updateList()).placeholder(UIKeys.SEARCH);

        this.slot = slot;
        this.wh(10 * TILE_SIZE, 170);

        this.count.relative(this).x(10).y(1F, -10).w(1F, -20).anchorY(1F);
        this.search.relative(this).x(10).y(10).w(1F, -20);

        this.add(this.count, this.search);

        this.inventory.scrollSpeed = TILE_SIZE;
        this.updateList();
        this.eventPropagataion(EventPropagation.BLOCK_INSIDE);
    }

    private void setCount(int count)
    {
        ItemStack stack = this.slot.getStack().copy();

        stack.setSize(count);
        this.slot.acceptStack(stack, this.slot.lastSlot);
    }

    private void updateList()
    {
        container.clear();

        for (ItemEntry entry : BBS.getItems().items.values())
        {
            Item item = entry.item;

            if (item.getId().toString().contains(this.search.getText()))
            {
                container.add(new ItemStack(entry));
            }
        }

        this.inventory.scroll = 0;
        this.inventory.scrollSize = (int) (Math.ceil(container.size() / 9D) * this.inventory.scrollItemSize);
    }

    private void setStack(ItemStack stack, int slot)
    {
        this.slot.acceptStack(stack, slot);

        this.fillStack(this.slot.getStack());
    }

    public void updateInventory()
    {
        this.inventory.scroll = 0;

        this.fillStack(this.slot.getStack());
    }

    private void fillStack(ItemStack stack)
    {
        this.count.setVisible(!stack.isEmpty());
        this.count.limit(1, stack.getMaxSize());
        this.count.setValue(stack.getSize());
    }

    @Override
    public void resize()
    {
        super.resize();

        int w = 9 * TILE_SIZE;
        int h = 5 * TILE_SIZE;

        this.inventory.set(this.area.mx(w), this.search.area.y + 25, w, h);
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (!this.area.isInside(context))
        {
            this.removeFromParent();

            return false;
        }

        if (this.inventory.mouseClicked(context))
        {
            return true;
        }

        boolean inventory = this.inventory.isInside(context);

        if (inventory && context.mouseButton == 0)
        {
            Area area = this.inventory;

            int x = (context.mouseX - area.x - 2) / TILE_SIZE;
            int y = (context.mouseY - area.y - 2) / TILE_SIZE;

            if (x >= 9 || y >= (this.inventory.h / TILE_SIZE) + 1 || x < 0 || y < 0 || !this.isVisible())
            {
                return true;
            }

            int index = x + y * 9;

            if (this.slot != null)
            {
                List<ItemStack> items = container;

                index += this.inventory.scroll / TILE_SIZE * 9;

                if (index < items.size())
                {
                    this.setStack(items.get(index), -1);
                    this.removeFromParent();
                }

                return true;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        return this.inventory.mouseScroll(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.inventory.mouseReleased(context);

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.active = null;

        /* Background rendering */
        context.batcher.box(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.WHITE);
        context.batcher.box(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, Colors.LIGHTEST_GRAY);

        int scroll = 0;

        if (container.size() > 45)
        {
            int rows = (int) Math.ceil(container.size() / 9F);
            float factor = this.inventory.scroll / (float) this.inventory.scrollSize;
            scroll = (int) (factor * rows);
            scroll *= 9;
        }

        int index = this.renderGrid(context, this.inventory, container, -1, scroll, scroll + this.inventory.h / TILE_SIZE * 9);

        if (index != -1)
        {
            this.active = container.get(index);
        }

        this.renderLockedArea(context);

        this.inventory.drag(context);

        context.batcher.clip(this.inventory, context);
        this.inventory.renderScrollbar(context.batcher);
        context.batcher.unclip(context);

        super.render(context);

        if (this.active != null)
        {
            context.tooltip.set(context, this);
        }
    }

    private int renderGrid(UIContext context, Area area, List<ItemStack> inventory, int index, int i, int c)
    {
        for (int j = 0; j < c - i; j ++)
        {
            int k = i + j;

            int x = j % 9;
            int y = j / 9;

            x = area.x + 2 + TILE_SIZE * x;
            y = area.y + 2 + TILE_SIZE * y;

            int diffX = context.mouseX - x;
            int diffY = context.mouseY - y;

            context.batcher.box(x - 1, y - 1, x + 17, y + 17, Colors.A25);

            if (k < inventory.size())
            {
                ItemStack stack = inventory.get(k);
                boolean hover = diffX >= 0 && diffX < 18 && diffY >= 0 && diffY < 18;

                if (hover)
                {
                    context.batcher.box(x - 2, y - 2, x + 18, y + 18, Colors.A75 | BBSSettings.primaryColor.get());
                    index = k;
                }

                BBS.getItems().renderInUI(context.render, stack, x, y, 16, 16);
            }
        }

        return index;
    }

    @Override
    public void renderTooltip(UIContext context, Area area)
    {
        super.renderTooltip(context, area);

        UIInventory.renderItemTooltip(context, this.active, context.mouseX, context.mouseY);
    }
}