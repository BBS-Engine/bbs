package mchorse.bbs.forms.forms;

import mchorse.bbs.forms.properties.BlockLinkProperty;
import mchorse.bbs.forms.renderers.BlockFormRenderer;
import mchorse.bbs.forms.renderers.FormRenderer;

public class BlockForm extends Form
{
    public BlockLinkProperty block = new BlockLinkProperty(this, "block", null);

    public BlockForm()
    {
        super();

        this.register(this.block);
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new BlockFormRenderer(this);
    }
}