package mchorse.bbs.game.items;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;

import java.util.Objects;

public class ItemStack implements IMapSerializable
{
    public static final ItemStack EMPTY = new ItemStack(new ItemEntry(new Item(Link.bbs("empty"))));

    private ItemEntry entry;
    private int size;
    public MapType data;

    private Form displayForm;
    private boolean displayFormCheck;

    public static ItemStack create(MapType data)
    {
        if (data == null || data.isEmpty())
        {
            return EMPTY;
        }

        ItemStack stack = new ItemStack();

        stack.fromData(data);

        return stack.isEmpty() ? EMPTY : stack;
    }

    public static boolean sameItem(ItemStack a, ItemStack b)
    {
        return a.entry == b.entry;
    }

    public static boolean equalMetadata(ItemStack a, ItemStack b)
    {
        return a.entry == b.entry;
    }

    public static boolean equal(ItemStack a, ItemStack b)
    {
        return a.entry == b.entry && Objects.equals(a.data, b.data);
    }

    public static boolean exact(ItemStack a, ItemStack b)
    {
        return a.entry == b.entry && Objects.equals(a.data, b.data) && a.size == b.size;
    }

    public ItemStack()
    {}

    public ItemStack(ItemEntry entry)
    {
        this(entry, 1);
    }

    public ItemStack(ItemEntry entry, int size)
    {
        this.entry = entry;
        this.size = size;
    }

    public Item getItem()
    {
        return this.entry.item;
    }

    public ItemRender getRender()
    {
        return this.entry.render;
    }

    public boolean isEmpty()
    {
        return this == EMPTY || this.size <= 0 || this.entry == null || this.entry == EMPTY.entry;
    }

    public void setSize(int size)
    {
        this.size = MathUtils.clamp(size, 0, this.getMaxSize());
    }

    public int getSize()
    {
        return this.size;
    }

    public int getMaxSize()
    {
        return this.entry == null ? 1 : this.entry.item.getMaxSize(this);
    }

    public Form getDisplayForm()
    {
        if (this.isEmpty())
        {
            return null;
        }

        if (this.displayForm == null && !this.displayFormCheck)
        {
            if (this.data != null && this.data.has("form"))
            {
                this.displayForm = BBS.getForms().fromData(this.data.getMap("form"));
            }

            if (this.displayForm == null && this.entry != null && this.entry.render.form != null)
            {
                this.displayForm = this.entry.render.form.copy();
            }

            this.displayFormCheck = true;
        }

        return this.displayForm;
    }

    public void setDisplayForm(Form displayForm)
    {
        this.displayForm = null;
        this.displayFormCheck = false;

        if (this.data == null)
        {
            this.data = new MapType();
        }

        if (displayForm == null)
        {
            this.data.remove("form");
        }
        else
        {
            this.data.put("form", FormUtils.toData(displayForm));
        }
    }

    public String getDisplayName()
    {
        return this.entry == null ? "-" : this.entry.item.getDisplayName(this);
    }

    public void setDisplayName(String displayName)
    {
        if (this.data == null)
        {
            this.data = new MapType();
        }

        this.data.putString("displayName", displayName);
    }

    public String getDescription()
    {
        return this.entry == null ? "" : this.entry.item.getDescription(this);
    }

    public void setDescription(String description)
    {
        if (this.data == null)
        {
            this.data = new MapType();
        }

        this.data.putString("description", description);
    }

    public int getFrameColor()
    {
        if (this.data != null && this.data.has("frameColor"))
        {
            return this.data.getInt("frameColor");
        }

        return this.entry == null ? Colors.LIGHTER_GRAY : this.entry.render.frameColor;
    }

    public void setFrameColor(int color)
    {
        if (this.data == null)
        {
            this.data = new MapType();
        }

        this.data.putInt("frameColor", color);
    }

    public void use(Entity player)
    {
        this.entry.item.use(player, this);
    }

    public ItemStack copy()
    {
        ItemStack copy = new ItemStack(this.entry, this.size);

        if (this.data != null)
        {
            copy.data = (MapType) this.data.copy();
        }

        return copy;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof ItemStack)
        {
            ItemStack stack = (ItemStack) obj;

            return this.entry == stack.entry
                && this.size == stack.size
                && Objects.equals(this.data, stack.data);
        }

        return false;
    }

    @Override
    public void toData(MapType data)
    {
        if (this.isEmpty())
        {
            return;
        }

        data.putString("id", this.entry.item.id.toString());
        data.putInt("size", this.size);

        if (this.data != null)
        {
            data.put("data", this.data);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("id"))
        {
            this.entry = BBS.getItems().get(Link.create(data.getString("id")));
        }

        this.size = data.getInt("size");

        if (data.has("data"))
        {
            this.data = data.getMap("data");
        }
    }
}