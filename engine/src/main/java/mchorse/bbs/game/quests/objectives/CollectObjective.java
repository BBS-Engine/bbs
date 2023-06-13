package mchorse.bbs.game.quests.objectives;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.world.entities.Entity;

public class CollectObjective extends Objective
{
    public ItemStack stack = ItemStack.EMPTY;
    public boolean ignoreData;

    public CollectObjective()
    {}

    public CollectObjective(ItemStack stack)
    {
        this.stack = stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public boolean isComplete(Entity player)
    {
        return this.countItems(player) >= this.stack.getSize();
    }

    @Override
    public void complete(Entity player)
    {
        PlayerComponent component = player.get(PlayerComponent.class);

        if (component != null)
        {
            component.inventory.removeSimilarItems(this.stack);
        }
    }

    private int countItems(Entity player)
    {
        PlayerComponent component = player.get(PlayerComponent.class);

        return component == null ? 0 : component.inventory.count(this.stack, true, this.ignoreData);
    }

    @Override
    public String stringifyObjective(Entity player)
    {
        String name = this.stack.getDisplayName();
        int count = Math.min(this.countItems(player), this.stack.getSize());

        if (!this.message.isEmpty())
        {
            return this.message.replace("${name}", name)
                .replace("${count}", String.valueOf(count))
                .replace("${total}", String.valueOf(this.stack.getSize()));
        }

        return UIKeys.QUESTS_OBJECTIVE_COLLECT_STRING.formatString(name, count, this.stack.getSize());
    }

    @Override
    public void partialToData(MapType data)
    {}

    @Override
    public void partialFromData(MapType data)
    {}

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (!this.stack.isEmpty())
        {
            data.put("item", this.stack.toData());
        }

        data.putBool("ignoreData", this.ignoreData);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("item"))
        {
            this.stack = ItemStack.create(data.getMap("item"));
        }

        this.ignoreData = data.getBool("ignoreData");
    }
}