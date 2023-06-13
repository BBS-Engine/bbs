package mchorse.bbs.camera.values;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;

public class ValueClip extends ValueGroup
{
    private Clip clip;

    public ValueClip(String id, Clip clip)
    {
        super(id);

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
        return BBS.getFactoryClips().toData(this.clip);
    }

    @Override
    public void fromData(BaseType data)
    {
        MapType map = data.asMap();
        Clip clip = BBS.getFactoryClips().fromData(map);

        if (clip != null)
        {
            this.assign(clip);
        }
    }
}