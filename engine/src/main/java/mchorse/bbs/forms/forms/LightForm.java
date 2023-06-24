package mchorse.bbs.forms.forms;

import mchorse.bbs.forms.properties.ColorProperty;
import mchorse.bbs.forms.properties.FloatProperty;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.forms.renderers.LightFormRenderer;
import mchorse.bbs.utils.colors.Color;

public class LightForm extends Form
{
    public final ColorProperty color = new ColorProperty(this, "color", Color.white());
    public final FloatProperty distance = new FloatProperty(this, "distance", 1F);

    public LightForm()
    {
        super();

        this.register(this.color);
        this.register(this.distance);
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new LightFormRenderer(this);
    }
}