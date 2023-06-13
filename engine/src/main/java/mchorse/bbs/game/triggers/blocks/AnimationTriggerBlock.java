package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.animation.AnimationPlayer;
import mchorse.bbs.animation.Animations;
import mchorse.bbs.bridge.IBridgeAnimations;
import mchorse.bbs.game.utils.DataContext;

public class AnimationTriggerBlock extends StringTriggerBlock
{
    @Override
    public void trigger(DataContext context)
    {
        Animations animations = context.world.bridge.get(IBridgeAnimations.class).getAnimations();
        AnimationPlayer player = animations.get(this.id);

        if (player == null)
        {
            animations.remove(this.id);
        }
        else
        {
            player.play();
        }
    }

    @Override
    protected String getKey()
    {
        return "animation";
    }
}