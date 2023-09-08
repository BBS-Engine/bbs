package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.math.MathUtils;

public class RemapperClip extends CameraClip
{
    public final KeyframeChannel channel = new KeyframeChannel("channel");

    public RemapperClip()
    {
        super();

        this.add(this.channel);

        this.channel.insert(0, 0);
        this.channel.insert(BBSSettings.getDefaultDuration(), 1);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        double factor = this.channel.interpolate(context.relativeTick + context.transition);
        int duration = this.duration.get();

        factor *= duration;
        factor = MathUtils.clamp(factor, 0, duration - 0.0001F);

        context.applyUnderneath(this.tick.get() + (int) factor, (float) (factor % 1), position);
    }

    @Override
    public Clip create()
    {
        return new RemapperClip();
    }

    @Override
    protected void breakDownClip(Clip original, int offset)
    {
        super.breakDownClip(original, offset);

        this.channel.moveX(-offset);
    }
}