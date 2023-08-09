package mchorse.bbs.animation.clip;

import mchorse.bbs.animation.AnimationPlayer;
import mchorse.bbs.animation.Animations;
import mchorse.bbs.bridge.IBridgeAnimations;
import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.settings.values.ValueString;

public class AnimationClip extends CameraClip
{
    public ValueString animation = new ValueString("animation", "");

    public AnimationClip()
    {
        super();

        this.register(this.animation);
    }

    @Override
    public void shutdown(ClipContext context)
    {
        super.shutdown(context);

        String key = this.animation.get();

        if (!key.isEmpty())
        {
            context.bridge.get(IBridgeAnimations.class).getAnimations().remove(this.animation.get());
        }
    }

    @Override
    public boolean isGlobal()
    {
        return true;
    }

    @Override
    protected void applyClip(ClipContext context, Position position)
    {
        String key = this.animation.get();

        if (key.isEmpty())
        {
            return;
        }

        Animations animations = context.bridge.get(IBridgeAnimations.class).getAnimations();
        AnimationPlayer player = animations.get(key);

        if (player != null && player.animation != null)
        {
            player.currentTime = context.relativeTick + (context.playing ? context.transition : 0);
            player.visible = player.currentTime >= 0 && player.currentTime < player.animation.duration;
        }
    }

    @Override
    protected Clip create()
    {
        return new AnimationClip();
    }
}