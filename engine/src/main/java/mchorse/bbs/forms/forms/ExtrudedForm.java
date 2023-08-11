package mchorse.bbs.forms.forms;

import mchorse.bbs.forms.properties.LinkProperty;
import mchorse.bbs.forms.renderers.ExtrudedFormRenderer;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.resources.Link;

public class ExtrudedForm extends Form
{
    public final LinkProperty texture = new LinkProperty(this, "texture", null);

    public ExtrudedForm()
    {
        super();

        this.register(this.texture);
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new ExtrudedFormRenderer(this);
    }

    @Override
    public String getDefaultDisplayName()
    {
        Link link = this.texture.get();

        return link == null ? "none" : link.toString();
    }
}