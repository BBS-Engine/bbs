package mchorse.bbs.utils.keyframes.generic.serializers;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.utils.colors.Color;

public class ColorKeyframeSerializer implements IGenericKeyframeSerializer<Color>
{
    @Override
    public Color fromData(BaseType data)
    {
        if (!data.isNumeric())
        {
            return new Color();
        }

        return Color.rgba(data.asNumeric().intValue());
    }

    @Override
    public BaseType toData(Color value)
    {
        return new IntType(value.getARGBColor());
    }

    @Override
    public Color copy(Color value)
    {
        return value.copy();
    }
}