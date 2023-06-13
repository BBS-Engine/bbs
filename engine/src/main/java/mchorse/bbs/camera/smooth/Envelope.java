package mchorse.bbs.camera.smooth;

import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.camera.values.ValueInterpolation;
import mchorse.bbs.camera.values.ValueKeyframeChannel;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;

public class Envelope extends StructureBase
{
    public final ValueBoolean enabled = new ValueBoolean("enabled");

    public final ValueFloat fadeIn = new ValueFloat("fadeIn", 10F);
    public final ValueFloat fadeOut = new ValueFloat("fadeOut", 10F);

    public final ValueInterpolation interpolation = new ValueInterpolation("interpolation");

    public final ValueBoolean keyframes = new ValueBoolean("keyframes");
    public final ValueKeyframeChannel channel = new ValueKeyframeChannel("channel");

    public Envelope()
    {
        this.register(this.enabled);
        this.register(this.fadeIn);
        this.register(this.fadeOut);
        this.register(this.interpolation);
        this.register(this.keyframes);
        this.register(this.channel);

        this.channel.get().insert(0, 0);
        this.channel.get().insert(BBSSettings.getDefaultDuration(), 1);
    }

    public Envelope copy()
    {
        Envelope envelope = new Envelope();

        envelope.copy(this);

        return envelope;
    }

    public float getStartX(int duration)
    {
        return 0;
    }

    public float getStartDuration(int duration)
    {
        return this.fadeIn.get();
    }

    public float getEndX(int duration)
    {
        return duration;
    }

    public float getEndDuration(int duration)
    {
        return duration - this.fadeOut.get();
    }

    public float factorEnabled(int duration, float tick)
    {
        if (!this.enabled.get())
        {
            return 1;
        }

        return this.factor(duration, tick);
    }

    public float factor(int duration, float tick)
    {
        float envelope = 0;

        if (this.keyframes.get())
        {
            if (!this.channel.get().isEmpty())
            {
                envelope = MathUtils.clamp((float) this.channel.get().interpolate(tick), 0, 1);
            }
        }
        else
        {
            envelope = Interpolations.envelope(tick, 0, this.fadeIn.get(), this.getEndDuration(duration), this.getEndX(duration));
            envelope = this.interpolation.get().interpolate(0, 1, envelope);
        }

        return envelope;
    }

    public void breakDown(Clip original, int offset)
    {
        Envelope originalEnvelope = original.envelope.get();

        this.fadeIn.set(0F);
        originalEnvelope.fadeOut.set(0F);

        this.channel.get().moveX(-offset);
    }
}