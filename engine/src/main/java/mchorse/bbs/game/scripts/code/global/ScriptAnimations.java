package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.animation.AnimationPlayer;
import mchorse.bbs.bridge.IBridgeAnimations;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.user.global.IScriptAnimations;

public class ScriptAnimations implements IScriptAnimations
{
    private ScriptBBS factory;

    public ScriptAnimations(ScriptBBS factory)
    {
        this.factory = factory;
    }

    @Override
    public boolean play(String id)
    {
        AnimationPlayer player = this.factory.getBridge().get(IBridgeAnimations.class).getAnimations().get(id);

        if (player == null)
        {
            this.factory.getBridge().get(IBridgeAnimations.class).getAnimations().remove(id);
        }
        else
        {
            player.play();
        }

        return player != null && player.animation != null;
    }

    @Override
    public boolean isPlaying(String id)
    {
        AnimationPlayer player = this.factory.getBridge().get(IBridgeAnimations.class).getAnimations().animations.get(id);

        return player != null && player.playing;
    }

    @Override
    public void stop(String id)
    {
        this.factory.getBridge().get(IBridgeAnimations.class).getAnimations().remove(id);
    }
}