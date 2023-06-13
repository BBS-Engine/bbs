package mchorse.bbs.forms.properties;

import mchorse.bbs.animation.IPuppet;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

import java.util.Map;
import java.util.Set;

public class TransformProperty extends BaseTweenProperty<Transform> implements IPuppet
{
    private Transform i = new Transform();
    private Transform frozen;

    public TransformProperty(Form form, String key, Transform value)
    {
        super(form, key, value);
    }

    @Override
    public void freeze()
    {
        this.frozen = this.value.copy();
    }

    @Override
    public void getAvailableKeys(String prefix, Set<String> keys)
    {
        String key = this.getKey();

        keys.add(IPuppet.combinePaths(prefix, key + ".x"));
        keys.add(IPuppet.combinePaths(prefix, key + ".y"));
        keys.add(IPuppet.combinePaths(prefix, key + ".z"));
        keys.add(IPuppet.combinePaths(prefix, key + ".sx"));
        keys.add(IPuppet.combinePaths(prefix, key + ".sy"));
        keys.add(IPuppet.combinePaths(prefix, key + ".sz"));
        keys.add(IPuppet.combinePaths(prefix, key + ".rx"));
        keys.add(IPuppet.combinePaths(prefix, key + ".ry"));
        keys.add(IPuppet.combinePaths(prefix, key + ".rz"));
    }

    @Override
    public void applyKeyframes(String prefix, Map<String, KeyframeChannel> keyframes, float ticks)
    {
        String key = this.getKey();

        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".x"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".y"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".z"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".sx"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".sy"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".sz"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".rx"), keyframes, ticks, true);
        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".ry"), keyframes, ticks, true);
        this.applyKeyframe(IPuppet.combinePaths(prefix, key + ".rz"), keyframes, ticks, true);
    }

    private void applyKeyframe(String key, Map<String, KeyframeChannel> keyframes, float ticks, boolean rads)
    {
        KeyframeChannel channel = keyframes.get(key);

        if (channel != null)
        {
            this.frozen.applyKeyframe(channel, key, ticks, rads);
        }
    }

    @Override
    public boolean fillDefaultValue(String prefix, ValueDouble value)
    {
        String start = IPuppet.combinePaths(prefix, this.getKey() + ".");

        if (value.getId().startsWith(start) && value.getId().indexOf('.', start.length()) == -1)
        {
            if (this.frozen.fillDefaultValue(value, value.getId(), false))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public Transform get(float transition)
    {
        if (this.frozen != null)
        {
            return this.frozen;
        }

        return super.get(transition);
    }

    @Override
    protected Transform getTweened(float transition)
    {
        float factor = this.interpolation.interpolate(0, 1, this.getTweenFactor(transition));

        this.i.copy(this.lastValue);
        this.i.lerp(this.value, factor);

        return this.i;
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.value.fromData(data.getMap(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.put(this.getKey(), this.value.toData());
    }
}