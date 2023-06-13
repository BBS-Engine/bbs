package mchorse.bbs.animation;

import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnimationModel implements IMapSerializable
{
    public static final Set<String> KEYS = CollectionUtils.setOf("x", "y", "z", "yaw", "head_yaw", "pitch");

    public Form form;
    public Map<String, KeyframeChannel> keyframes = new HashMap<String, KeyframeChannel>();

    private Entity dummy;

    public void insert(Vector3d position)
    {
        KeyframeChannel x = new KeyframeChannel();
        KeyframeChannel y = new KeyframeChannel();
        KeyframeChannel z = new KeyframeChannel();

        x.insert(0, position.x);
        y.insert(0, position.y);
        z.insert(0, position.z);

        this.keyframes.put("x", x);
        this.keyframes.put("y", y);
        this.keyframes.put("z", z);
    }

    private Entity getDummy()
    {
        if (this.dummy == null)
        {
            this.dummy = EntityArchitect.createDummy();
        }

        return this.dummy;
    }

    private double interpolate(String key, float ticks, double defaultValue)
    {
        KeyframeChannel channel = this.keyframes.get(key);

        if (channel != null && !channel.isEmpty())
        {
            return channel.interpolate(ticks);
        }

        return defaultValue;
    }

    public double getDefaultValue(String key)
    {
        ValueDouble value = new ValueDouble(key, 0.0);

        value.set(0.0);

        if (this.form instanceof IPuppet)
        {
            ((IPuppet) this.form).fillDefaultValue("", value);
        }

        return value.get();
    }

    public Set<String> getAvailableKeys()
    {
        Set<String> set = new HashSet<String>();

        set.addAll(KEYS);

        if (this.form instanceof IPuppet)
        {
            ((IPuppet) this.form).getAvailableKeys("", set);
        }

        return set;
    }

    public void render(RenderingContext context, float currentTicks)
    {
        if (this.form == null)
        {
            return;
        }

        Entity dummy = this.getDummy();

        this.updateFormAndEntity(dummy, currentTicks);

        context.stack.push();
        context.stack.multiply(dummy.getMatrixForRenderWithRotation(context.getCamera(), context.getTransition()));

        this.form.getRenderer().render(dummy, context);

        context.stack.pop();
    }

    public void renderForStencil(RenderingContext context, float currentTicks, float partialTicks)
    {
        if (this.form == null)
        {
            return;
        }

        Entity dummy = this.getDummy();

        this.updateFormAndEntity(dummy, currentTicks);

        context.stack.push();
        context.stack.multiply(dummy.getMatrixForRenderWithRotation(context.getCamera(), context.getTransition()));

        this.form.getRenderer().render(this.getDummy(), context);

        context.stack.pop();
    }

    private void updateFormAndEntity(Entity entity, float currentTicks)
    {
        entity.basic.setPosition(
            this.interpolate("x", currentTicks, 0),
            this.interpolate("y", currentTicks, 0),
            this.interpolate("z", currentTicks, 0)
        );

        entity.basic.rotation.set(
            (float) Math.toRadians(this.interpolate("pitch", currentTicks, 0)),
            (float) Math.toRadians(this.interpolate("head_yaw", currentTicks, 0)),
            (float) Math.toRadians(this.interpolate("yaw", currentTicks, 0))
        );

        entity.basic.rotation.y += entity.basic.rotation.z;
        entity.basic.prevPosition.set(entity.basic.position);
        entity.basic.prevRotation.set(entity.basic.rotation);

        IPuppet.freeze(this.form);
        IPuppet.applyKeyframes(this.form, "", this.keyframes, currentTicks);
    }

    @Override
    public void toData(MapType data)
    {
        if (this.form != null)
        {
            data.put("form", FormUtils.toData(this.form));
        }

        MapType keyframes = new MapType();

        for (Map.Entry<String, KeyframeChannel> entry : this.keyframes.entrySet())
        {
            keyframes.put(entry.getKey(), entry.getValue().toData());
        }

        data.put("keyframes", keyframes);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("form"))
        {
            this.form = FormUtils.fromData(data.getMap("form"));
        }

        if (data.has("keyframes"))
        {
            MapType keyframes = data.getMap("keyframes");

            for (String key : keyframes.keys())
            {
                ListType keyframeData = keyframes.getList(key);
                KeyframeChannel channel = new KeyframeChannel();

                channel.fromData(keyframeData);
                this.keyframes.put(key, channel);
            }
        }
    }
}