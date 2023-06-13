package mchorse.bbs.game.items;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.world.entities.Entity;

public class ItemTrigger extends Item
{
    public Trigger useTrigger = new Trigger();

    @Override
    public void use(Entity user, ItemStack stack)
    {
        this.useTrigger.trigger(user);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("useTrigger", this.useTrigger.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.useTrigger.fromData(data.getMap("useTrigger"));
    }
}