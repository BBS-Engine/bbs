package mchorse.bbs.game.scripts.code.entities;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.scripts.code.ScriptRayTrace;
import mchorse.bbs.game.scripts.user.IScriptRayTrace;
import mchorse.bbs.game.scripts.user.entities.IScriptEntity;
import mchorse.bbs.game.states.States;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ScriptEntity implements IScriptEntity
{
    protected Entity entity;

    public static IScriptEntity create(Entity entity)
    {
        if (EntityUtils.isPlayer(entity))
        {
            return new ScriptPlayer(entity);
        }
        else if (entity != null)
        {
            return new ScriptEntity(entity);
        }

        return null;
    }

    protected ScriptEntity(Entity entity)
    {
        this.entity = entity;
    }

    @Override
    public Entity getRawEntity()
    {
        return this.entity;
    }

    /* Entity properties */

    @Override
    public Vector3d getPosition()
    {
        return new Vector3d(this.entity.basic.position);
    }

    @Override
    public void setPosition(double x, double y, double z)
    {
        this.entity.setPosition(x, y, z);
    }

    @Override
    public Vector3f getVelocity()
    {
        return new Vector3f(this.entity.basic.velocity);
    }

    @Override
    public void setVelocity(float x, float y, float z)
    {
        this.entity.basic.velocity.set(x, y, z);
    }

    @Override
    public Vector3f getRotations()
    {
        return new Vector3f(this.entity.basic.rotation);
    }

    @Override
    public void setRotations(float pitch, float yaw, float yawHead)
    {
        this.entity.basic.rotation.set(pitch, yaw, yawHead);
    }

    @Override
    public Vector3f getLook()
    {
        return new Vector3f(this.entity.basic.getLook());
    }

    @Override
    public float getWidth()
    {
        return (float) this.entity.basic.hitbox.w;
    }

    @Override
    public float getHeight()
    {
        return (float) this.entity.basic.hitbox.h;
    }

    @Override
    public boolean isSneaking()
    {
        return this.entity.basic.sneak;
    }

    @Override
    public IScriptRayTrace rayTrace(double maxDistance)
    {
        return ScriptRayTrace.traceFromEntity(this.entity, false, maxDistance);
    }

    @Override
    public IScriptRayTrace rayTraceBlock(double maxDistance)
    {
        return ScriptRayTrace.traceFromEntity(this.entity, true, maxDistance);
    }

    @Override
    public ItemStack getMainItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void setMainItem(ItemStack stack)
    {}

    @Override
    public ItemStack getOffItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void setOffItem(ItemStack stack)
    {}

    /* Entity meta */

    @Override
    public String getUniqueId()
    {
        return this.entity.getUUID().toString();
    }

    @Override
    public String getEntityId()
    {
        return this.entity.id.toString();
    }

    @Override
    public int getTicks()
    {
        return this.entity.basic.ticks;
    }

    @Override
    public String getName()
    {
        return this.entity.basic.name;
    }

    @Override
    public MapType getFullData()
    {
        return this.entity.toData();
    }

    @Override
    public void setFullData(MapType data)
    {
        this.entity.fromData(data);
    }

    @Override
    public boolean isPlayer()
    {
        return EntityUtils.isPlayer(this.entity);
    }

    @Override
    public boolean isNpc()
    {
        return EntityUtils.isNpc(this.entity);
    }

    @Override
    public boolean isSame(IScriptEntity entity)
    {
        return this.entity == entity.getRawEntity();
    }

    @Override
    public void remove()
    {
        this.entity.remove();
    }

    @Override
    public void kill()
    {
        this.remove(); /* TODO: Mappet */
    }

    /* Mappet stuff */

    @Override
    public States getStates()
    {
        return null;
    }

    @Override
    public Form getForm()
    {
        FormComponent component = this.entity.get(FormComponent.class);

        return component == null ? null : component.form;
    }

    @Override
    public boolean setForm(Form form)
    {
        FormComponent component = this.entity.get(FormComponent.class);

        if (component != null)
        {
            component.form = form;

            return true;
        }

        return false;
    }

    @Override
    public Form getFirstPersonForm()
    {
        FormComponent component = this.entity.get(FormComponent.class);

        return component == null ? null : component.firstPersonForm;
    }

    @Override
    public boolean setFirstPersonForm(Form form)
    {
        FormComponent component = this.entity.get(FormComponent.class);

        if (component != null)
        {
            component.firstPersonForm = form;

            return true;
        }

        return false;
    }
}