package mchorse.bbs.forms.properties;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

public class FloatProperty extends BaseTweenProperty<Float>
{
    public FloatProperty(Form form, String key, Float value)
    {
        super(form, key, value, KeyframeFactories.FLOAT);
    }
}