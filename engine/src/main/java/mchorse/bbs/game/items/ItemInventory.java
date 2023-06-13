package mchorse.bbs.game.items;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;

import java.util.ArrayList;
import java.util.List;

public class ItemInventory implements IMapSerializable
{
    public final List<ItemStack> stacks;

    protected int size;

    public ItemInventory()
    {
        this(20);
    }

    public ItemInventory(int size)
    {
        this.size = size;
        this.stacks = new ArrayList<ItemStack>(size);

        for (int i = 0; i < size; i++)
        {
            this.stacks.add(ItemStack.EMPTY);
        }
    }

    public int getSize()
    {
        return this.size;
    }

    public ItemStack getStack(int i)
    {
        return i >= 0 && i < this.stacks.size() ? this.stacks.get(i) : ItemStack.EMPTY;
    }

    /**
     * Adds an item stack in this inventory to the first available index. If a
     * similar item found with same item and matching data, then the size
     * would be summed, but only if the capacity of the item stack allows
     * it.
     *
     * <p>Note: make sure to provide a <b>copy of an item stack</b> as this
     * method doesn't copies the item!</p>
     */
    public boolean addStack(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return false;
        }

        for (int i = 0; i < this.stacks.size(); i++)
        {
            ItemStack target = this.stacks.get(i);
            int total = target.getSize() + stack.getSize();

            if (ItemStack.equal(stack, target) && total <= target.getMaxSize())
            {
                target.setSize(total);

                return true;
            }
            else if (target.isEmpty())
            {
                this.stacks.set(i, stack);

                return true;
            }
        }

        return false;
    }

    /**
     * Set an item stack in this inventory at specified index. It fully
     * replaces the item stack.
     *
     * <p>Note: make sure to provide a <b>copy of an item stack</b> as this
     * method doesn't copies the item!</p>
     */
    public boolean setStack(int index, ItemStack stack)
    {
        if (index < 0 || index >= this.getSize())
        {
            return false;
        }

        this.stacks.set(index, stack);

        return false;
    }

    /**
     * Checks whether given item stack is present in the given size in this
     * inventory.
     *
     * <p><b>Important:</b> it doesn't looks for exactly the same
     * item stack but rather for similar item stacks (item and data) and so
     * their total sum would be more or equal to size of given item stack.</p>
     */
    public boolean has(ItemStack target)
    {
        int count = this.count(target, true);

        return count >= target.getSize();
    }

    public int count(ItemStack target)
    {
        return this.count(target, false);
    }

    public int count(ItemStack target, boolean stop)
    {
        return this.count(target, stop, false);
    }

    public int count(ItemStack target, boolean stop, boolean ignoreData)
    {
        int c = 0;

        for (ItemStack stack : this.stacks)
        {
            if (
                (ignoreData && ItemStack.equal(target, stack)) ||
                (!ignoreData && ItemStack.sameItem(target, stack))
            ) {
                c += stack.getSize();
            }

            if (stop && c >= target.getSize())
            {
                return c;
            }
        }

        return c;
    }

    public void removeSimilarItems(ItemStack target)
    {
        int toRemove = target.getSize();

        for (int i = 0, c = this.stacks.size(); i < c; i++)
        {
            ItemStack stack = this.stacks.get(i);

            if (ItemStack.equal(target, stack))
            {
                int size = stack.getSize();

                if (size > toRemove)
                {
                    stack.setSize(size - toRemove);
                }
                else
                {
                    this.stacks.set(i, ItemStack.EMPTY);
                }

                toRemove -= size;
            }

            if (toRemove <= 0)
            {
                break;
            }
        }
    }

    @Override
    public void toData(MapType data)
    {
        ListType items = new ListType();
        boolean empty = true;

        for (ItemStack stack : this.stacks)
        {
            items.add(stack.toData());

            if (!stack.isEmpty())
            {
                empty = false;
            }
        }

        if (!empty)
        {
            data.put("items", items);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("items"))
        {
            ListType items = data.getList("items");

            for (int i = 0, c = this.getSize(); i < c; i++)
            {
                ItemStack stack = i < items.size() ? ItemStack.create(items.getMap(i)) : ItemStack.EMPTY;

                this.stacks.set(i, stack);
            }
        }
    }
}