package mchorse.bbs.forms.forms;

import mchorse.bbs.animation.IPuppet;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BodyPart implements IMapSerializable, IPuppet
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

    public Transform puppetTransform;

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

        Transform transform = this.puppetTransform == null ? this.transform : this.puppetTransform;

        context.stack.push();
        context.stack.multiply(transform.createMatrix());

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

    @Override
    public void freeze()
    {
        this.puppetTransform = this.transform.copy();

        IPuppet.freeze(this.form);
    }

    @Override
    public void getAvailableKeys(String prefix, Set<String> keys)
    {
        keys.add(IPuppet.combinePaths(prefix, "x"));
        keys.add(IPuppet.combinePaths(prefix, "y"));
        keys.add(IPuppet.combinePaths(prefix, "z"));
        keys.add(IPuppet.combinePaths(prefix, "sx"));
        keys.add(IPuppet.combinePaths(prefix, "sy"));
        keys.add(IPuppet.combinePaths(prefix, "sz"));
        keys.add(IPuppet.combinePaths(prefix, "rx"));
        keys.add(IPuppet.combinePaths(prefix, "ry"));
        keys.add(IPuppet.combinePaths(prefix, "rz"));

        IPuppet.getAvailableKeys(this.form, prefix, keys);
    }

    @Override
    public void applyKeyframes(String prefix, Map<String, KeyframeChannel> keyframes, float ticks)
    {
        this.applyKeyframe(IPuppet.combinePaths(prefix, "x"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, "y"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, "z"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, "sx"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, "sy"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, "sz"), keyframes, ticks, false);
        this.applyKeyframe(IPuppet.combinePaths(prefix, "rx"), keyframes, ticks, true);
        this.applyKeyframe(IPuppet.combinePaths(prefix, "ry"), keyframes, ticks, true);
        this.applyKeyframe(IPuppet.combinePaths(prefix, "rz"), keyframes, ticks, true);

        IPuppet.applyKeyframes(this.form, prefix, keyframes, ticks);
    }

    private void applyKeyframe(String key, Map<String, KeyframeChannel> keyframes, float ticks, boolean rads)
    {
        KeyframeChannel channel = keyframes.get(key);

        if (channel != null)
        {
            this.puppetTransform.applyKeyframe(channel, key, ticks, rads);
        }
    }

    @Override
    public boolean fillDefaultValue(String prefix, ValueDouble value)
    {
        String start = IPuppet.combinePaths(prefix, "");

        if (value.getId().startsWith(start) && value.getId().indexOf('.', start.length()) == -1)
        {
            if (this.transform.fillDefaultValue(value, value.getId(), false))
            {
                return true;
            }
        }

        return IPuppet.fillDefaultValue(this.form, prefix, value);
    }

    public void tween(BodyPart part, int duration, IInterpolation interpolation)
    {
        if (this.form != null && part.form != null)
        {
            this.form.tween(part.form, duration, interpolation);
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