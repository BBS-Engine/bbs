package mchorse.bbs.forms.forms;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormArchitect;
import mchorse.bbs.forms.properties.BooleanProperty;
import mchorse.bbs.forms.properties.FloatProperty;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.forms.properties.StringProperty;
import mchorse.bbs.forms.properties.TransformProperty;
import mchorse.bbs.forms.renderers.FormRenderer;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.world.entities.Entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Form implements IMapSerializable
{
    private Form parent;

    public final StringProperty name = new StringProperty(this, "name", "");
    public final TransformProperty transform = new TransformProperty(this, "transform", new Transform());
    public final BodyPartManager parts = new BodyPartManager(this);
    public final StringProperty anchor = new StringProperty(this, "anchor", "");

    /* Hitbox properties */
    public final BooleanProperty hitbox = new BooleanProperty(this, "hitbox", false);
    public final FloatProperty hitboxWidth = new FloatProperty(this, "hitboxWidth", 0.5F);
    public final FloatProperty hitboxHeight = new FloatProperty(this, "hitboxHeight", 1.8F);
    public final FloatProperty hitboxSneakMultiplier = new FloatProperty(this, "hitboxSneakMultiplier", 0.9F);
    public final FloatProperty hitboxEyeHeight = new FloatProperty(this, "hitboxEyeHeight", 0.9F);

    protected FormRenderer renderer;
    protected String cachedID;
    protected final Map<String, IFormProperty> properties = new HashMap<>();

    public Form()
    {
        this.name.cantAnimate();

        this.register(this.name);
        this.register(this.transform);
        this.register(this.anchor);

        this.hitbox.cantAnimate();
        this.hitboxWidth.cantAnimate();
        this.hitboxHeight.cantAnimate();
        this.hitboxSneakMultiplier.cantAnimate();
        this.hitboxEyeHeight.cantAnimate();

        this.register(this.hitbox);
        this.register(this.hitboxWidth);
        this.register(this.hitboxHeight);
        this.register(this.hitboxSneakMultiplier);
        this.register(this.hitboxEyeHeight);
    }

    protected void register(IFormProperty property)
    {
        if (this.properties.containsKey(property.getKey()))
        {
            throw new IllegalStateException("Property " + property.getKey() + " was already registered for form by ID " + this.getId() + "!");
        }

        this.properties.put(property.getKey(), property);
    }

    public Map<String, IFormProperty> getProperties()
    {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Only body parts can set form's parent.
     */
    void setParent(Form parent)
    {
        this.parent = parent;
    }

    public Form getParent()
    {
        return this.parent;
    }

    public FormRenderer getRenderer()
    {
        if (this.renderer == null)
        {
            this.renderer = this.createRenderer();
        }

        return this.renderer;
    }

    protected abstract FormRenderer createRenderer();

    /* ID and display name */

    public String getId()
    {
        if (this.cachedID == null)
        {
            this.cachedID = BBS.getForms().getType(this).toString();
        }

        return this.cachedID;
    }

    public String getIdOrName()
    {
        String name = this.name.get();

        return name.isEmpty() ? this.getId() : name;
    }

    public final String getDisplayName()
    {
        String name = this.name.get();

        if (!name.isEmpty())
        {
            return name;
        }

        return this.getDefaultDisplayName();
    }

    protected String getDefaultDisplayName()
    {
        return this.getId();
    }

    /* Update */

    public void update(Entity entity)
    {
        this.updateHitbox(entity);

        this.parts.update(entity);

        for (IFormProperty property : this.properties.values())
        {
            property.update();
        }
    }

    public void updateHitbox(Entity entity)
    {
        if (this.hitbox.get() && this.parent == null)
        {
            entity.basic.hitboxWidth = this.hitboxWidth.get();
            entity.basic.hitboxHeight = this.hitboxHeight.get();
            entity.basic.eyeHeight = this.hitboxEyeHeight.get();
            entity.basic.sneakMultiplier = this.hitboxSneakMultiplier.get();
        }
    }

    /* Tweening */

    public void tween(Form form, int duration, IInterpolation interpolation, int offset, boolean playing)
    {
        for (IFormProperty property : this.properties.values())
        {
            IFormProperty formProperty = form.properties.get(property.getKey());

            if (formProperty != null)
            {
                property.tween(formProperty.get(), property.get(), duration, interpolation, offset, playing);
            }
        }

        this.parts.tween(form.parts, duration, interpolation, offset, playing);
    }

    /* Data comparison and (de)serialization */

    public final Form copy()
    {
        FormArchitect forms = BBS.getForms();

        return forms.fromData(forms.toData(this));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof Form)
        {
            Form form = (Form) obj;

            if (!this.parts.equals(form.parts))
            {
                return false;
            }

            if (this.properties.size() != form.properties.size())
            {
                return false;
            }

            for (String key : this.properties.keySet())
            {
                if (!this.properties.get(key).equals(form.properties.get(key)))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void toData(MapType data)
    {
        data.put("bodyParts", this.parts.toData());

        for (IFormProperty property : this.properties.values())
        {
            property.toData(data);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        this.parts.fromData(data.getMap("bodyParts"));

        for (IFormProperty property : this.properties.values())
        {
            property.fromData(data);
        }
    }
}