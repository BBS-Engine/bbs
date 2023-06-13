package mchorse.bbs.game.quests.rewards;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class ItemStackReward extends Reward
{
    public List<ItemStack> stacks = new ArrayList<ItemStack>();

    public ItemStackReward()
    {}

    public ItemStackReward(ItemStack... stacks)
    {
        for (ItemStack stack : stacks)
        {
            this.stacks.add(stack);
        }
    }

    @Override
    public void reward(Entity player)
    {
        PlayerComponent character = player.get(PlayerComponent.class);

        for (ItemStack stack : this.stacks)
        {
            ItemStack copy = stack.copy();

            if (!copy.isEmpty() && !character.inventory.addStack(copy))
            {
                character.dropItem(copy);
            }
        }
    }

    @Override
    public Reward copy()
    {
        ItemStackReward reward = new ItemStackReward();

        for (ItemStack stack : this.stacks)
        {
            reward.stacks.add(stack.copy());
        }

        return reward;
    }

    @Override
    public void toData(MapType data)
    {
        ListType items = new ListType();

        data.put("items", items);

        for (ItemStack stack : this.stacks)
        {
            if (!stack.isEmpty())
            {
                items.add(stack.toData());
            }
        }
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("items"))
        {
            ListType items = data.getList("items");

            for (int i = 0; i < items.size(); i ++)
            {
                ItemStack stack = ItemStack.create(items.getMap(i));

                if (!stack.isEmpty())
                {
                    this.stacks.add(stack);
                }
            }
        }
    }
}