package mchorse.bbs.forms.forms;

import mchorse.bbs.forms.properties.BooleanProperty;
import mchorse.bbs.forms.properties.IntegerProperty;
import mchorse.bbs.forms.properties.LinkProperty;
import mchorse.bbs.forms.renderers.CameraFormRenderer;
import mchorse.bbs.forms.renderers.FormRenderer;

public class CameraForm extends Form
{
    public final LinkProperty texture = new LinkProperty(this, "texture", null);
    public final BooleanProperty enabled = new BooleanProperty(this, "enabled", false);
    public final IntegerProperty width = new IntegerProperty(this, "width", 512);
    public final IntegerProperty height = new IntegerProperty(this, "height", 512);

    public CameraForm()
    {
        this.register(this.texture);
        this.register(this.enabled);
        this.register(this.width);
        this.register(this.height);
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new CameraFormRenderer(this);
    }
}
