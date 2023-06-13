package mchorse.bbs.game.scripts.code.entities;

import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.items.ItemInventory;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.quests.Quests;
import mchorse.bbs.game.scripts.user.entities.IScriptPlayer;
import mchorse.bbs.game.states.States;
import mchorse.bbs.world.entities.Entity;

public class ScriptPlayer extends ScriptEntity implements IScriptPlayer
{
    public ScriptPlayer(Entity entity)
    {
        super(entity);
    }

    @Override
    public ItemStack getMainItem()
    {
        return this.getEquipment().getStack(0);
    }

    @Override
    public void setMainItem(ItemStack stack)
    {
        this.getEquipment().setStack(0, stack);
    }

    @Override
    public ItemStack getOffItem()
    {
        return this.getEquipment().getStack(1);
    }

    @Override
    public void setOffItem(ItemStack stack)
    {
        this.getEquipment().setStack(1, stack);
    }

    @Override
    public ItemInventory getInventory()
    {
        return this.entity.get(PlayerComponent.class).inventory;
    }

    @Override
    public ItemInventory getEquipment()
    {
        return this.entity.get(PlayerComponent.class).equipment;
    }

    @Override
    public States getStates()
    {
        return this.entity.get(PlayerComponent.class).states;
    }

    @Override
    public Quests getQuests()
    {
        return this.entity.get(PlayerComponent.class).quests;
    }

    @Override
    public boolean canControl()
    {
        return this.entity.get(PlayerComponent.class).canControl;
    }

    @Override
    public void setControl(boolean canControl)
    {
        this.entity.get(PlayerComponent.class).canControl = canControl;
    }
}