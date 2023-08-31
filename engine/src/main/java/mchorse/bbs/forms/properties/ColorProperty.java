package mchorse.bbs.forms.properties;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

public class ColorProperty extends BaseTweenProperty<Color>
{
    public ColorProperty(Form form, String key, Color value)
    {
        super(form, key, value, KeyframeFactories.COLOR);
    }
}