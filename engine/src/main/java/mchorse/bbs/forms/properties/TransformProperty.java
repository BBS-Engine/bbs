package mchorse.bbs.forms.properties;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.pose.Transform;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

public class TransformProperty extends BaseTweenProperty<Transform>
{
    public TransformProperty(Form form, String key, Transform value)
    {
        super(form, key, value, KeyframeFactories.TRANSFORM);
    }
}