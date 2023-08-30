package mchorse.bbs.forms.forms;

import mchorse.bbs.forms.properties.BooleanProperty;
import mchorse.bbs.forms.properties.ColorProperty;
import mchorse.bbs.forms.properties.FloatProperty;
import mchorse.bbs.forms.properties.LinkProperty;
import mchorse.bbs.forms.properties.Vector4fProperty;
import mchorse.bbs.forms.renderers.BillboardFormRenderer;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.colors.Color;
import org.joml.Vector4f;

public class BillboardForm extends Form
{
    public final LinkProperty texture = new LinkProperty(this, "texture", null);
    public final BooleanProperty billboard = new BooleanProperty(this, "billboard", false);
    public final Vector4fProperty crop = new Vector4fProperty(this, "crop", new Vector4f(0, 0, 0, 0));
    public final BooleanProperty resizeCrop = new BooleanProperty(this, "resizeCrop", false);
    public final ColorProperty color = new ColorProperty(this, "color", Color.white());
    public final FloatProperty offsetX = new FloatProperty(this, "offsetX", 0F);
    public final FloatProperty offsetY = new FloatProperty(this, "offsetY", 0F);
    public final FloatProperty rotation = new FloatProperty(this, "rotation", 0F);

    public BillboardForm()
    {
        super();

        this.resizeCrop.cantAnimate();

        this.register(this.texture);
        this.register(this.billboard);
        this.register(this.crop);
        this.register(this.resizeCrop);
        this.register(this.color);
        this.register(this.offsetX);
        this.register(this.offsetY);
        this.register(this.rotation);
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new BillboardFormRenderer(this);
    }

    @Override
    public String getDefaultDisplayName()
    {
        Link link = this.texture.get();

        return link == null ? "none" : link.toString();
    }
}