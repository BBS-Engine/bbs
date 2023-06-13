package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EnumUtils;
import mchorse.bbs.game.utils.Target;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.graphics.text.Font;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.world.entities.Entity;

public class ItemTriggerBlock extends TriggerBlock
{
    public Target target = new Target(TargetMode.SUBJECT);
    public ItemStack stack = ItemStack.EMPTY;
    public ItemMode mode = ItemMode.TAKE;

    @Override
    public String stringify()
    {
        String displayName = this.stack.getDisplayName();

        if (this.stack.getSize() > 1)
        {
            displayName += " (" + Font.FORMAT_YELLOW + this.stack.getSize() + Font.FORMAT_RESET + ")";
        }

        if (this.mode == ItemMode.GIVE)
        {
            return UIKeys.NODES_ITEM_GIVE.formatString(displayName);
        }

        return UIKeys.NODES_ITEM_TAKE.formatString(displayName);
    }

    @Override
    public void trigger(DataContext context)
    {
        Entity player;

        if (this.stack.isEmpty() || (player = this.target.getPlayer(context)) == null)
        {
            context.cancel();

            return;
        }

        PlayerComponent character = player.get(PlayerComponent.class);

        /* Give the item stack to player */
        if (this.mode == ItemMode.GIVE)
        {
            ItemStack copy = this.stack.copy();

            if (!copy.isEmpty() && !character.inventory.addStack(copy))
            {
                character.dropItem(copy);
            }

            return;
        }

        if (character.inventory.has(this.stack))
        {
            character.inventory.removeSimilarItems(this.stack);
        }
        else
        {
            context.cancel();
        }
    }

    @Override
    public boolean isEmpty()
    {
        return this.stack.isEmpty();
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("target", this.target.toData());
        data.put("stack", this.stack.toData());
        data.putInt("mode", this.mode.ordinal());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("target"))
        {
            this.target.fromData(data.getMap("target"));
        }

        if (data.has("stack"))
        {
            this.stack = ItemStack.create(data.getMap("stack"));
        }

        if (data.has("mode"))
        {
            this.mode = EnumUtils.getValue(data.getInt("mode"), ItemMode.values(), ItemMode.TAKE);
        }
    }

    public static enum ItemMode
    {
        TAKE, GIVE
    }
}