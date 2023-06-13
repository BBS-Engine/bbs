package mchorse.bbs.game.crafting;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.conditions.Condition;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemInventory;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipe implements IMapSerializable
{
    public String title = "";
    public String description = "";
    public List<ItemStack> input = new ArrayList<ItemStack>();
    public List<ItemStack> output = new ArrayList<ItemStack>();
    public Condition visible = new Condition(true);
    public int hotkey = -1;
    public Trigger trigger = new Trigger();

    public boolean isAvailable(Entity player)
    {
        return this.visible.execute(new DataContext(player));
    }

    public boolean craft(Entity player)
    {
        return this.craft(player, null);
    }

    public boolean craft(Entity player, DataContext context)
    {
        if (context == null)
        {
            context = new DataContext(player);
        }

        if (!this.isPlayerHasAllItems(player))
        {
            return false;
        }

        for (ItemStack stack : this.input)
        {
            player.get(PlayerComponent.class).inventory.removeSimilarItems(stack);
        }

        for (ItemStack stack : this.output)
        {
            this.addOrDrop(player, stack.copy());
        }

        this.trigger.trigger(context);

        return true;
    }

    public boolean isPlayerHasAllItems(Entity player)
    {
        ItemInventory inventory = player.get(PlayerComponent.class).inventory;

        for (ItemStack stack : this.input)
        {
            if (!inventory.has(stack))
            {
                return false;
            }
        }

        return true;
    }

    private void addOrDrop(Entity player, ItemStack stack)
    {
        PlayerComponent character = player.get(PlayerComponent.class);

        if (!character.inventory.addStack(stack))
        {
            character.dropItem(stack);
        }
    }

    @Override
    public void toData(MapType data)
    {
        ListType input = this.listToData(this.input);
        ListType output = this.listToData(this.output);

        if (!this.title.isEmpty())
        {
            data.putString("title", this.title);
        }

        if (!this.description.isEmpty())
        {
            data.putString("description", this.description);
        }

        if (input != null)
        {
            data.put("input", input);
        }

        if (output != null)
        {
            data.put("output", output);
        }

        data.put("visible", this.visible.toData());

        MapType trigger = this.trigger.toData();

        if (!trigger.isEmpty())
        {
            data.put("trigger", trigger);
        }

        if (this.hotkey > 0)
        {
            data.putInt("hotkey", this.hotkey);
        }
    }

    private ListType listToData(List<ItemStack> list)
    {
        ListType listType = new ListType();

        for (ItemStack stack : list)
        {
            if (!stack.isEmpty())
            {
                listType.add(stack.toData());
            }
        }

        return listType.isEmpty() ? null : listType;
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("title"))
        {
            this.title = data.getString("title");
        }

        if (data.has("description"))
        {
            this.description = data.getString("description");
        }

        if (data.has("input"))
        {
            this.listFromData(this.input, data.getList("input"));
        }

        if (data.has("output"))
        {
            this.listFromData(this.output, data.getList("output"));
        }

        if (data.has("visible"))
        {
            this.visible.fromData(data.getMap("visible"));
        }

        if (data.has("trigger"))
        {
            this.trigger.fromData(data.getMap("trigger"));
        }

        if (data.has("hotkey"))
        {
            this.hotkey = data.getInt("hotkey");
        }
    }

    private void listFromData(List<ItemStack> list, ListType listType)
    {
        list.clear();

        for (int i = 0; i < listType.size(); i++)
        {
            MapType map = listType.getMap(i);
            ItemStack stack = ItemStack.create(map);

            if (!stack.isEmpty())
            {
                list.add(stack);
            }
        }
    }
}