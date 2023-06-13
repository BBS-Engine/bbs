package mchorse.bbs.forms.properties;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.utils.resources.MultiLink;

public class LinkProperty extends BaseTweenProperty<Link>
{
    public LinkProperty(Form form, String key, Link value)
    {
        super(form, key, value);
    }

    @Override
    protected Link getTweened(float transition)
    {
        if (!this.canAnimate())
        {
            return this.value;
        }

        Integer lastFrame = this.extractFrame(this.lastValue.path);
        Integer currentFrame = this.extractFrame(this.value.path);

        if (lastFrame != null && currentFrame != null)
        {
            int frame = (int) this.interpolation.interpolate(lastFrame, currentFrame, this.getTweenFactor(transition));

            return new Link(this.value.source, this.replaceFrame(this.value.path, frame));
        }

        return this.value;
    }

    private boolean canAnimate()
    {
        if (this.value == null || this.lastValue == null)
        {
            return false;
        }

        if (this.value instanceof MultiLink || this.lastValue instanceof MultiLink)
        {
            return false;
        }

        return this.value.source.equals(this.lastValue.source);
    }

    private Integer extractFrame(String path)
    {
        int lastUnderscore = path.lastIndexOf('_');
        int lastDot = path.lastIndexOf('.', lastUnderscore);

        if (lastUnderscore < 0 || lastDot < 0)
        {
            return null;
        }

        try
        {
            return Integer.parseInt(path.substring(lastUnderscore + 1, lastDot));
        }
        catch (Exception e)
        {}

        return null;
    }

    private String replaceFrame(String path, int frame)
    {
        int lastUnderscore = path.lastIndexOf('_');
        int lastDot = path.lastIndexOf('.', lastUnderscore);

        if (lastUnderscore < 0 || lastDot < 0)
        {
            return null;
        }

        return path.substring(0, lastUnderscore + 1) + frame + path.substring(lastDot);
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.set(LinkUtils.create(data.get(key)));
    }

    @Override
    public void toData(MapType data)
    {
        data.put(this.getKey(), LinkUtils.toData(this.value));
    }
}