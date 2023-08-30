package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.serializers.KeyframeSerializers;

import java.util.HashSet;
import java.util.Set;

public class PoseProperty extends BaseTweenProperty<Pose>
{
    private static Set<String> keys = new HashSet<>();

    private Pose i = new Pose();

    public PoseProperty(Form form, String key, Pose value)
    {
        super(form, key, value);
    }

    @Override
    protected Pose getTweened(float transition)
    {
        float factor = this.interpolation.interpolate(0, 1, this.getTweenFactor(transition));

        keys.clear();

        if (this.lastValue != null)
        {
            keys.addAll(this.lastValue.transforms.keySet());
        }

        if (this.value != null)
        {
            keys.addAll(this.value.transforms.keySet());
        }

        this.i.copy(this.lastValue);

        for (String key : keys)
        {
            Transform transform = this.i.get(key);
            Transform t = this.value.get(key);

            transform.lerp(t, factor);
        }

        return this.i;
    }

    @Override
    public boolean canCreateChannel()
    {
        return this.canAnimate;
    }

    @Override
    public GenericKeyframeChannel createChannel()
    {
        return new GenericKeyframeChannel(KeyframeSerializers.POSE);
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