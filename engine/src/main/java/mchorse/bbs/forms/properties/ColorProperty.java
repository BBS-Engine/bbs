package mchorse.bbs.forms.properties;

import mchorse.bbs.animation.IPuppet;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

import java.util.Map;
import java.util.Set;

public class ColorProperty extends BaseTweenProperty<Color> implements IPuppet
{
    private Color i = new Color();
    private Color frozen;

    public ColorProperty(Form form, String key, Color value)
    {
        super(form, key, value);
    }

    @Override
    public void freeze()
    {
        this.frozen = new Color().copy(this.value);
    }

    @Override
    public void getAvailableKeys(String prefix, Set<String> keys)
    {
        String key = this.getKey();

        keys.add(IPuppet.combinePaths(prefix, key + ".r"));
        keys.add(IPuppet.combinePaths(prefix, key + ".g"));
        keys.add(IPuppet.combinePaths(prefix, key + ".b"));
        keys.add(IPuppet.combinePaths(prefix, key + ".a"));
    }

    @Override
    public void applyKeyframes(String prefix, Map<String, KeyframeChannel> keyframes, float ticks)
    {
        String key = this.getKey();
        String r = IPuppet.combinePaths(prefix, key + ".r");
        String g = IPuppet.combinePaths(prefix, key + ".g");
        String b = IPuppet.combinePaths(prefix, key + ".b");
        String a = IPuppet.combinePaths(prefix, key + ".a");

        if (keyframes.containsKey(r))
        {
            this.frozen.r = (float) keyframes.get(r).interpolate(ticks);
        }

        if (keyframes.containsKey(g))
        {
            this.frozen.g = (float) keyframes.get(g).interpolate(ticks);
        }

        if (keyframes.containsKey(b))
        {
            this.frozen.b = (float) keyframes.get(b).interpolate(ticks);
        }

        if (keyframes.containsKey(a))
        {
            this.frozen.a = (float) keyframes.get(a).interpolate(ticks);
        }
    }

    @Override
    public boolean fillDefaultValue(String prefix, ValueDouble value)
    {
        String id = value.getId();
        String key = this.getKey();

        if (id.equals(IPuppet.combinePaths(prefix, key + ".r")))
        {
            value.set((double) this.value.r);

            return true;
        }
        else if (id.equals(IPuppet.combinePaths(prefix, key + ".g")))
        {
            value.set((double) this.value.g);

            return true;
        }
        else if (id.equals(IPuppet.combinePaths(prefix, key + ".b")))
        {
            value.set((double) this.value.b);

            return true;
        }
        else if (id.equals(IPuppet.combinePaths(prefix, key + ".a")))
        {
            value.set((double) this.value.a);

            return true;
        }

        return false;
    }

    @Override
    public Color get(float transition)
    {
        if (this.frozen != null)
        {
            return this.frozen;
        }

        return super.get(transition);
    }

    @Override
    protected Color getTweened(float transition)
    {
        float factor = this.getTweenFactor(transition);

        this.i.r = this.interpolation.interpolate(this.lastValue.r, this.value.r, factor);
        this.i.g = this.interpolation.interpolate(this.lastValue.g, this.value.g, factor);
        this.i.b = this.interpolation.interpolate(this.lastValue.b, this.value.b, factor);
        this.i.a = this.interpolation.interpolate(this.lastValue.a, this.value.a, factor);

        return this.i;
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(new Color().set(data.getInt(key)));
    }

    @Override
    public void toData(MapType data)
    {
        data.putInt(this.getKey(), this.value.getARGBColor());
    }
}