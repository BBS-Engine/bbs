package mchorse.bbs.forms.properties;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

public class IntegerProperty extends BaseTweenProperty<Integer>
{
    public IntegerProperty(Form form, String key, Integer value)
    {
        super(form, key, value, KeyframeFactories.INTEGER);
    }
}