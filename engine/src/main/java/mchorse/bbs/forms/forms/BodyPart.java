package mchorse.bbs.forms.forms;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;

import java.util.Objects;

public class BodyPart implements IMapSerializable
{
    /**
     * This body part's owner.
     */
    private BodyPartManager manager;

    private Form form;
    private Transform transform = new Transform();

    public String bone = "";
    public boolean enabled = true;
    public boolean useTarget;

    private Entity entity = EntityArchitect.createDummy();

    void setManager(BodyPartManager manager)
    {
        this.manager = manager;

        if (this.form != null)
        {
            this.form.setParent(manager == null ? null : manager.getOwner());
        }
    }

    public Form getForm()
    {
        return this.form;
    }

    public void setForm(Form form)
    {
        if (this.form != null)
        {
            this.form.setParent(null);
        }

        this.form = form;

        if (this.form != null && this.manager != null)
        {
            this.form.setParent(this.manager.getOwner());
        }
    }

    public Transform getTransform()
    {
        return this.transform;
    }

    public void render(Entity target, RenderingContext context)
    {
        if (this.form == null || !this.enabled)
        {
            return;
        }

        context.stack.push();
        context.stack.multiply(this.transform.createMatrix());

        this.form.getRenderer().render(this.useTarget ? target : this.entity, context);

        context.stack.pop();
    }

    public void update(Entity target)
    {
        if (this.form != null)
        {
            this.form.update(this.useTarget ? target : this.entity);
        }
    }

    public BodyPart copy()
    {
        BodyPart part = new BodyPart();

        part.fromData(this.toData());

        return part;
    }

    public void tween(BodyPart part, int duration, IInterpolation interpolation, int offset, boolean playing)
    {
        if (this.form != null && part.form != null)
        {
            this.form.tween(part.form, duration, interpolation, offset, playing);
        }

        this.transform.copy(part.transform);
        this.bone = part.bone;
        this.enabled = part.enabled;
        this.useTarget = part.useTarget;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof BodyPart)
        {
            BodyPart bodyPart = (BodyPart) obj;

            return Objects.equals(this.form, bodyPart.form)
                && Objects.equals(this.bone, bodyPart.bone)
                && Objects.equals(this.transform, bodyPart.transform)
                && this.enabled == bodyPart.enabled
                && this.useTarget == bodyPart.useTarget;
        }

        return false;
    }

    @Override
    public void toData(MapType data)
    {
        if (this.form != null)
        {
            data.put("form", FormUtils.toData(this.form));
        }

        data.put("transform", this.transform.toData());
        data.putString("bone", this.bone);
        data.putBool("enabled", this.enabled);
        data.putBool("useTarget", this.useTarget);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("form"))
        {
            this.setForm(FormUtils.fromData(data.getMap("form")));
        }

        this.transform.fromData(data.getMap("transform"));
        this.bone = data.getString("bone");
        this.enabled = data.getBool("enabled");
        this.useTarget = data.getBool("useTarget");
    }
}