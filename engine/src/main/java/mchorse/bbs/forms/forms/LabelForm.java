package mchorse.bbs.forms.forms;

import mchorse.bbs.forms.properties.BooleanProperty;
import mchorse.bbs.forms.properties.ColorProperty;
import mchorse.bbs.forms.properties.FloatProperty;
import mchorse.bbs.forms.properties.IntegerProperty;
import mchorse.bbs.forms.properties.LinkProperty;
import mchorse.bbs.forms.properties.StringProperty;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.forms.renderers.LabelFormRenderer;
import mchorse.bbs.utils.colors.Color;

public class LabelForm extends Form
{
    public final StringProperty text = new StringProperty(this, "text", "Hello, World!");
    public final LinkProperty font = new LinkProperty(this, "font", null);
    public final ColorProperty color = new ColorProperty(this, "color", Color.white());

    public final IntegerProperty max = new IntegerProperty(this, "max", -1);
    public final FloatProperty anchorX = new FloatProperty(this, "anchorX", 0.5F);
    public final FloatProperty anchorY = new FloatProperty(this, "anchorY", 0.5F);
    public final BooleanProperty anchorLines = new BooleanProperty(this, "anchorLines", false);

    /* Shadow properties */
    public final FloatProperty shadowX = new FloatProperty(this, "shadowX", 1F);
    public final FloatProperty shadowY = new FloatProperty(this, "shadowY", 1F);
    public final ColorProperty shadowColor = new ColorProperty(this, "shadowColor", new Color(0, 0, 0, 0));

    /* Background */
    public final ColorProperty background = new ColorProperty(this, "background", new Color(0, 0, 0, 0));
    public final FloatProperty offset = new FloatProperty(this, "offset", 3F);

    public LabelForm()
    {
        super();

        this.font.cantAnimate();

        this.register(this.text);
        this.register(this.font);
        this.register(this.color);
        this.register(this.max);
        this.register(this.anchorX);
        this.register(this.anchorY);
        this.register(this.anchorLines);
        this.register(this.shadowX);
        this.register(this.shadowY);
        this.register(this.shadowColor);
        this.register(this.background);
        this.register(this.offset);
    }

    @Override
    protected FormRenderer createRenderer()
    {
        return new LabelFormRenderer(this);
    }

    @Override
    public String getDefaultDisplayName()
    {
        return this.text.get();
    }
}