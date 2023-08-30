package mchorse.bbs.utils.keyframes.generic.serializers;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;

public class LinkKeyframeSerializer implements IGenericKeyframeSerializer<Link>
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
}