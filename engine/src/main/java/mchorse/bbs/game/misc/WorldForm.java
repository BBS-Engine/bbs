package mchorse.bbs.game.misc;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.UUID;

public class WorldForm
{
    public Form form;
    public Vector3d position = new Vector3d();
    public Vector3f rotation = new Vector3f();
    public Entity entity;
    public int expiration;
    public boolean rotate;

    private UUID entityId;
    private Entity dummy;

    public boolean update(World world)
    {
        Entity dummy = this.getDummy(world);

        this.form.update(dummy);
        dummy.basic.ticks += 1;

        return dummy.basic.ticks > this.expiration;
    }

    public void render(RenderingContext context)
    {
        Entity dummy = this.getDummy(context.getWorld());

        double x = Interpolations.lerp(dummy.basic.prevPosition.x, dummy.basic.position.x, context.getTransition());
        double y = Interpolations.lerp(dummy.basic.prevPosition.y, dummy.basic.position.y, context.getTransition());
        double z = Interpolations.lerp(dummy.basic.prevPosition.z, dummy.basic.position.z, context.getTransition());

        context.stack.push();
        context.stack.translateRelative(context.getCamera(), x, y, z);

        this.form.getRenderer().render(dummy, context);

        context.stack.pop();
    }

    private Entity getDummy(World world)
    {
        if (this.dummy == null)
        {
            this.dummy = EntityArchitect.createDummy();
        }

        Entity entity = this.getEntity(world);

        if (entity == null)
        {
            this.dummy.basic.position.set(this.position);
            this.dummy.basic.prevPosition.set(this.position);
            this.dummy.basic.rotation.set(this.rotation);
            this.dummy.basic.prevRotation.set(this.rotation);
        }
        else
        {
            this.dummy.basic.position.set(entity.basic.position).add(this.position);

            if (entity.isRemoved())
            {
                this.dummy.basic.prevPosition.set(this.dummy.basic.position);

                if (this.rotate)
                {
                    this.dummy.basic.rotation.set(entity.basic.rotation).add(this.rotation);
                    this.dummy.basic.prevRotation.set(this.dummy.basic.rotation);
                }
            }
            else
            {
                this.dummy.basic.prevPosition.set(entity.basic.prevPosition).add(this.position);

                if (this.rotate)
                {
                    this.dummy.basic.rotation.set(entity.basic.rotation).add(this.rotation);
                    this.dummy.basic.prevRotation.set(entity.basic.prevRotation).add(this.rotation);
                }
                else
                {
                    this.dummy.basic.rotation.set(this.rotation);
                    this.dummy.basic.prevRotation.set(this.dummy.basic.rotation);
                }
            }
        }

        return this.dummy;
    }

    private Entity getEntity(World world)
    {
        if (this.entityId != null && this.entity == null)
        {
            this.entity = world.getEntityByUUID(this.entityId);
        }

        return this.entity;
    }
}