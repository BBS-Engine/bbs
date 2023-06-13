package mchorse.bbs.forms.forms;

import mchorse.bbs.forms.properties.IntegerProperty;
import mchorse.bbs.forms.properties.ItemStackProperty;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.forms.renderers.ItemFormRenderer;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.world.entities.Entity;

public class ItemForm extends Form
{
    public final ItemStackProperty stack = new ItemStackProperty(this, "stack", ItemStack.EMPTY);
    public final IntegerProperty slot = new IntegerProperty(this, "slot", -1);

    private ItemStack lastStack;
    private ItemStack copy;

    public ItemForm()
    {
        super();

        this.register(this.stack);
        this.register(this.slot);
    }

    public ItemStack getStack(Entity entity)
    {
        ItemStack stack = ItemStack.EMPTY;
        int slot = this.slot.get();

        if (slot < 0)
        {
            stack = this.stack.get();
        }
        else if (entity != null && entity.has(PlayerComponent.class))
        {
            PlayerComponent player = entity.get(PlayerComponent.class);

            stack = player.equipment.getStack(slot);
        }

        if (this.lastStack != stack)
        {
            this.lastStack = stack;
            this.copy = stack.copy();
        }

        return this.copy;
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new ItemFormRenderer(this);
    }

    @Override
    public void update(Entity entity)
    {
        ItemStack stack = this.getStack(entity);

        if (stack != null)
        {
            Form form = stack.getDisplayForm();

            if (form != null)
            {
                form.update(entity);
            }
        }

        super.update(entity);
    }
}