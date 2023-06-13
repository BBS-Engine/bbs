package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.code.ScriptWorld;
import mchorse.bbs.game.scripts.user.IScriptWorld;
import mchorse.bbs.game.scripts.user.global.IScriptWorlds;
import mchorse.bbs.world.entities.Entity;

public class ScriptWorlds implements IScriptWorlds
{
    private ScriptBBS factory;

    public ScriptWorlds(ScriptBBS factory)
    {
        this.factory = factory;
    }

    @Override
    public boolean load(String world)
    {
        return this.factory.getBridge().get(IBridgeWorld.class).loadWorld(world);
    }

    @Override
    public boolean loadAt(String world, double x, double y, double z, float pitch, float yaw)
    {
        boolean loaded = this.factory.getBridge().get(IBridgeWorld.class).loadWorld(world);

        if (loaded)
        {
            Entity player = this.factory.getBridge().get(IBridgePlayer.class).getController();

            if (player != null)
            {
                player.setPosition(x, y, z);
                player.setRotation(pitch, yaw);
            }
        }

        return loaded;
    }

    @Override
    public IScriptWorld getCurrent()
    {
        return new ScriptWorld(this.factory.getBridge().get(IBridgeWorld.class).getWorld());
    }
}