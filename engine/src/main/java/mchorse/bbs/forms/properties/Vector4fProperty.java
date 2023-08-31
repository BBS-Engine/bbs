package mchorse.bbs.forms.properties;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;
import org.joml.Vector4f;

public class Vector4fProperty extends BaseTweenProperty<Vector4f>
{
    public Vector4fProperty(Form form, String key, Vector4f value)
    {
        super(form, key, value, KeyframeFactories.VECTOR4F);
    }
}