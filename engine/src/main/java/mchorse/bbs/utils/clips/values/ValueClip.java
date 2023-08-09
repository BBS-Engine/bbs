package mchorse.bbs.utils.clips.values;

import mchorse.bbs.camera.clips.ClipFactoryData;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.factory.IFactory;

public class ValueClip extends ValueGroup
{
    private Clip clip;
    private IFactory<Clip, ClipFactoryData> factory;

    public ValueClip(String id, Clip clip, IFactory<Clip, ClipFactoryData> factory)
    {
        super(id);

        this.factory = factory;

        this.assign(clip);
    }

    /* New value methods */

    public void assign(Clip clip)
    {
        this.clip = clip;

        this.removeAll();

        if (clip != null)
        {
            for (BaseValue value : clip.getProperties())
            {
                this.add(value);
            }
        }
    }

    public Clip get()
    {
        return this.clip;
    }

    public void set(Clip clip)
    {
        if (clip != null)
        {
            this.assign(clip.copy());
        }
    }

    /* Value implementation */

    @Override
    public void reset()
    {
        this.assign(null);
    }

    @Override
    public BaseType toData()
    {
        return this.factory.toData(this.clip);
    }

    @Override
    public void fromData(BaseType data)
    {
        MapType map = data.asMap();
        Clip clip = this.factory.fromData(map);

        if (clip != null)
        {
            this.assign(clip);
        }
    }
}