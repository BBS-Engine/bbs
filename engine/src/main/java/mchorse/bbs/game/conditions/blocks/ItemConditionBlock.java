package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EnumUtils;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.world.entities.Entity;

public class ItemConditionBlock extends TargetConditionBlock
{
    public ItemStack stack = ItemStack.EMPTY;
    public ItemCheck check = ItemCheck.HELD;

    @Override
    public boolean evaluateBlock(DataContext context)
    {
        if (this.target.mode != TargetMode.GLOBAL)
        {
            Entity player = this.target.getPlayer(context);

            if (player != null)
            {
                PlayerComponent character = player.get(PlayerComponent.class);

                if (this.check == ItemCheck.HELD)
                {
                    ItemStack main = character.equipment.getStack(0);
                    ItemStack off = character.equipment.getStack(1);

                    boolean a = ItemStack.exact(main, this.stack);
                    boolean b = ItemStack.exact(off, this.stack);

                    return a || b;
                }
                else if (this.check == ItemCheck.EQUIPMENT)
                {
                    return character.equipment.has(this.stack);
                }

                return character.inventory.has(this.stack);
            }
        }

        return false;
    }

    @Override
    protected TargetMode getDefaultTarget()
    {
        return TargetMode.SUBJECT;
    }

    @Override
    public String stringify()
    {
        String name = this.stack.getDisplayName();

        if (this.check == ItemCheck.HELD)
        {
            return UIKeys.CONDITIONS_ITEM_HOLDS.formatString(name);
        }
        else if (this.check == ItemCheck.EQUIPMENT)
        {
            return UIKeys.CONDITIONS_ITEM_EQUIPMENT.formatString(name);
        }

        return UIKeys.CONDITIONS_ITEM_INVENTORY.formatString(name + "x" + this.stack.getSize());
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("stack", this.stack.toData());
        data.putInt("check", this.check.ordinal());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.stack = ItemStack.create(data.getMap("stack"));
        this.check = EnumUtils.getValue(data.getInt("check"), ItemCheck.values(), ItemCheck.HELD);
    }

    public static enum ItemCheck
    {
        HELD, EQUIPMENT, INVENTORY
    }
}