package mchorse.bbs.game.items;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.world.entities.Entity;

public class Item implements IMapSerializable
{
    protected Link id;
    protected String displayName = "";
    protected String description = "";
    protected int maxStack = 99;

    public Item()
    {}

    public Item(Link id)
    {
        this.id = id;
    }

    public Link getId()
    {
        return this.id;
    }

    public void setId(Link id)
    {
        this.id = id;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDisplayName(ItemStack stack)
    {
        if (stack.data != null && stack.data.has("displayName"))
        {
            return stack.data.getString("displayName");
        }

        if (!this.displayName.isEmpty())
        {
            return this.displayName;
        }

        return this.getId().toString();
    }

    public String getDescription(ItemStack stack)
    {
        if (stack.data != null && stack.data.has("description"))
        {
            return stack.data.getString("description");
        }

        return this.description;
    }

    public int getMaxStack()
    {
        return this.maxStack;
    }

    public void setMaxStack(int maxStack)
    {
        this.maxStack = maxStack;
    }

    public int getMaxSize(ItemStack stack)
    {
        return this.maxStack;
    }

    public void use(Entity user, ItemStack stack)
    {}

    @Override
    public void toData(MapType data)
    {
        data.putString("displayName", this.displayName);
        data.putString("description", this.description);
        data.putInt("maxStack", this.maxStack);
    }

    @Override
    public void fromData(MapType data)
    {
        this.displayName = data.getString("displayName");
        this.description = data.getString("description");
        this.maxStack = data.getInt("maxStack", this.maxStack);
    }
}