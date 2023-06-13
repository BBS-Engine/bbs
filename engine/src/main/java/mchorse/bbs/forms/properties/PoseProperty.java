package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.Transform;

import java.util.HashSet;
import java.util.Set;

public class PoseProperty extends BaseTweenProperty<Pose>
{
    private Pose i = new Pose();
    private Set<String> keys = new HashSet<String>();

    public PoseProperty(Form form, String key, Pose value)
    {
        super(form, key, value);
    }

    @Override
    protected Pose getTweened(float transition)
    {
        float factor = this.interpolation.interpolate(0, 1, this.getTweenFactor(transition));
        Transform empty = new Transform();

        this.keys.clear();

        if (this.lastValue != null)
        {
            this.keys.addAll(this.lastValue.transforms.keySet());
        }

        if (this.value != null)
        {
            this.keys.addAll(this.value.transforms.keySet());
        }

        this.i.copy(this.lastValue);

        for (String key : this.keys)
        {
            Transform transform = this.i.transforms.get(key);

            if (transform == null)
            {
                transform = new Transform();

                this.i.transforms.put(key, transform);
            }

            Transform t = this.value.transforms.get(key);

            transform.lerp(t == null ? empty : t, factor);
        }

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