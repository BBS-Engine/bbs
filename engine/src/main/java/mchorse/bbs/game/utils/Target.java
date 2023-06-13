package mchorse.bbs.game.utils;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.BBSData;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.states.States;

public class Target implements IMapSerializable
{
    public TargetMode mode;
    public String selector = "";

    private TargetMode defaultMode;

    public Target(TargetMode mode)
    {
        this.mode = this.defaultMode = mode;
    }

    public Entity getPlayer(DataContext context)
    {
        if (this.mode == TargetMode.SUBJECT && EntityUtils.isPlayer(context.subject))
        {
            return context.subject;
        }
        else if (this.mode == TargetMode.OBJECT && EntityUtils.isPlayer(context.object))
        {
            return context.object;
        }
        else if (this.mode == TargetMode.PLAYER)
        {
            return context.getPlayer();
        }
        else if (this.mode == TargetMode.SELECTOR)
        {
            return null; /* TODO: Mappet */
        }

        return null;
    }

    public Entity getEntity(DataContext context)
    {
        if (this.mode == TargetMode.SUBJECT && context.subject != null)
        {
            return context.subject;
        }
        else if (this.mode == TargetMode.OBJECT && context.object != null)
        {
            return context.object;
        }
        else if (this.mode == TargetMode.PLAYER)
        {
            return context.getPlayer();
        }
        else if (this.mode == TargetMode.NPC)
        {
            return context.getNpc();
        }
        else if (this.mode == TargetMode.SELECTOR)
        {
            return null; /* TODO: Mappet */
        }

        return null;
    }

    public PlayerComponent getCharacter(DataContext context)
    {
        return this.getPlayer(context).get(PlayerComponent.class);
    }

    public States getStates(DataContext context)
    {
        if (this.mode != TargetMode.GLOBAL)
        {
            return EntityUtils.getStates(this.getEntity(context));
        }

        return BBSData.getStates();
    }

    @Override
    public void toData(MapType data)
    {
        data.putInt("target", this.mode.ordinal());
        data.putString("selector", this.selector);
    }

    @Override
    public void fromData(MapType data)
    {
        this.mode = EnumUtils.getValue(data.getInt("target"), TargetMode.values(), this.defaultMode);
        this.selector = data.getString("selector");
    }
}