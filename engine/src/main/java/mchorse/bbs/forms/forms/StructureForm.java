package mchorse.bbs.forms.forms;

import mchorse.bbs.forms.properties.StringProperty;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.forms.renderers.StructureFormRenderer;

public class StructureForm extends Form
{
    public final StringProperty structure = new StringProperty(this, "structure", "");

    public StructureForm()
    {
        super();

        this.register(this.structure);
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new StructureFormRenderer(this);
    }
}