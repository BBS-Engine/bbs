package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.ui.film.replays.properties.factories.UILinkKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.resources.LinkUtils;

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
    public Link create()
    {
        return Link.bbs("nil");
    }

    @Override
    public UIKeyframeFactory<Link> createUI(GenericKeyframe<Link> keyframe, UIPropertyEditor editor)
    {
        return new UILinkKeyframeFactory(keyframe, editor);
    }
}