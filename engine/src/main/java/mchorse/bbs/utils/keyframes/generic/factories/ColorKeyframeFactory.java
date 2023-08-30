package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIColorKeyframeFactory;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

public class ColorKeyframeFactory implements IGenericKeyframeFactory<Color>
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

    @Override
    public Color create()
    {
        return new Color();
    }

    @Override
    public UIKeyframeFactory<Color> createUI(GenericKeyframe<Color> keyframe, UIPropertyEditor editor)
    {
        return new UIColorKeyframeFactory(keyframe, editor);
    }
}