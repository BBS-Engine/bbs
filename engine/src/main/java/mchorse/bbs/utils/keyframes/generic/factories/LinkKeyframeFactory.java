package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.ui.film.replays.properties.factories.UILinkKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.utils.resources.MultiLink;

public class LinkKeyframeFactory implements IGenericKeyframeFactory<Link>
{
    @Override
    public Link fromData(BaseType data)
    {
        return LinkUtils.create(data);
    }

    @Override
    public BaseType toData(Link value)
    {
        return LinkUtils.toData(value);
    }

    @Override
    public Link copy(Link value)
    {
        return LinkUtils.copy(value);
    }

    @Override
    public Link interpolate(Link a, Link b, IInterpolation interpolation, float x)
    {
        if (!this.canAnimate(a, b))
        {
            return b;
        }

        Integer lastFrame = this.extractFrame(a.path);
        Integer currentFrame = this.extractFrame(b.path);

        if (lastFrame != null && currentFrame != null)
        {
            int frame = Math.round(interpolation.interpolate(lastFrame, currentFrame, x));

            return new Link(b.source, this.replaceFrame(b.path, frame));
        }

        return b;
    }

    private boolean canAnimate(Link a, Link b)
    {
        if (b == null || a == null)
        {
            return false;
        }

        if (b instanceof MultiLink || a instanceof MultiLink)
        {
            return false;
        }

        return b.source.equals(a.source);
    }

    private Integer extractFrame(String path)
    {
        int lastUnderscore = path.lastIndexOf('_');
        int lastDot = path.lastIndexOf('.');

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
        int lastDot = path.lastIndexOf('.');

        if (lastUnderscore < 0 || lastDot < 0)
        {
            return null;
        }

        return path.substring(0, lastUnderscore + 1) + frame + path.substring(lastDot);
    }

    @Override
    public UIKeyframeFactory<Link> createUI(GenericKeyframe<Link> keyframe, UIPropertyEditor editor)
    {
        return new UILinkKeyframeFactory(keyframe, editor);
    }
}