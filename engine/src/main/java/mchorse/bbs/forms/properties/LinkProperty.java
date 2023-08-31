package mchorse.bbs.forms.properties;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;

public class LinkProperty extends BaseTweenProperty<Link>
{
    public LinkProperty(Form form, String key, Link value)
    {
        super(form, key, value, KeyframeFactories.LINK);
    }
}