package mchorse.bbs.forms.properties;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

public class PoseProperty extends BaseTweenProperty<Pose>
{
    public PoseProperty(Form form, String key, Pose value)
    {
        super(form, key, value, KeyframeFactories.POSE);
    }
}