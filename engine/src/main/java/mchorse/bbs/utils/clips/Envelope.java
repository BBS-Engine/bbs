package mchorse.bbs.utils.clips;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.camera.values.ValueInterpolation;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;

public class Envelope extends ValueGroup
{
    public final ValueBoolean enabled = new ValueBoolean("enabled");

    public final ValueFloat fadeIn = new ValueFloat("fadeIn", 10F);
    public final ValueFloat fadeOut = new ValueFloat("fadeOut", 10F);

    public final ValueInterpolation pre = new ValueInterpolation("pre");
    public final ValueInterpolation post = new ValueInterpolation("post");

    public final ValueBoolean keyframes = new ValueBoolean("keyframes");
    public final KeyframeChannel channel = new KeyframeChannel("channel");

    public Envelope(String id)
    {
        super(id);

        this.add(this.enabled);
        this.add(this.fadeIn);
        this.add(this.fadeOut);
        this.add(this.pre);
        this.add(this.post);
        this.add(this.keyframes);
        this.add(this.channel);

        this.channel.insert(0, 0);
        this.channel.insert(BBSSettings.getDefaultDuration(), 1);
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
            if (!this.channel.isEmpty())
            {
                envelope = MathUtils.clamp((float) this.channel.interpolate(tick), 0, 1);
            }
        }
        else
        {
            float lowOut = this.fadeIn.get();

            envelope = Interpolations.envelope(tick, 0, lowOut, this.getEndDuration(duration), this.getEndX(duration));
            envelope = (tick <= lowOut ? this.pre : this.post).get().interpolate(0, 1, envelope);
        }

        return envelope;
    }

    public void breakDown(Clip original, int offset)
    {
        this.fadeIn.set(0F);
        original.envelope.fadeOut.set(0F);

        this.channel.moveX(-offset);
    }

    @Override
    public void fromData(BaseType data)
    {
        super.fromData(data);

        if (data.isMap())
        {
            MapType map = data.asMap();

            if (map.has("interpolation"))
            {
                BaseType interpolation = map.get("interpolation");

                this.pre.fromData(interpolation);
                this.post.fromData(interpolation);
            }
        }
    }
}