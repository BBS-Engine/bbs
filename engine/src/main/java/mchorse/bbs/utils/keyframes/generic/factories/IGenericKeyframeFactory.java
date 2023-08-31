package mchorse.bbs.utils.keyframes.generic.factories;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.math.IInterpolation;

public interface IGenericKeyframeFactory <T>
{
    public T fromData(BaseType data);

    public BaseType toData(T value);

    public T copy(T value);

    public T interpolate(T a, T b, IInterpolation interpolation, float x);

    public UIKeyframeFactory<T> createUI(GenericKeyframe<T> keyframe, UIPropertyEditor editor);
}