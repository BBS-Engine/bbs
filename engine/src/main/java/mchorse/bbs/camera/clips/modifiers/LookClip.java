package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;
import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValuePoint;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.world.entities.Entity;

/**
 * Look modifier
 * 
 * This modifier locks fixture's angle so it would always look in the 
 * direction of entity. Relative yaw and pitch is also supported.
 */
public class LookClip extends EntityClip
{
    public final ValueBoolean relative = new ValueBoolean("relative");
    public final ValueBoolean atBlock = new ValueBoolean("atBlock");
    public final ValueBoolean forward = new ValueBoolean("forward");
    public final ValuePoint block = new ValuePoint("block", new Point(0, 0, 0));

    public LookClip()
    {
        super();

        this.register(this.relative);
        this.register(this.atBlock);
        this.register(this.forward);
        this.register(this.block);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        if (this.checkForDead())
        {
            this.tryFindingEntity(context.bridge.get(IBridgeWorld.class).getWorld());
        }

        boolean atBlock = this.atBlock.get();
        boolean forward = this.forward.get();

        if (this.entities == null && !(atBlock || forward))
        {
            return;
        }

        boolean wasModified = false;

        if (forward && context.applyUnderneath(context.ticks - 1, context.transition, this.position))
        {
            wasModified = true;
        }

        if (!wasModified)
        {
            this.position.copy(position);
        }

        double x = 0;
        double y = 0;
        double z = 0;

        if (atBlock)
        {
            Point block = this.block.get();

            x = block.x;
            y = block.y;
            z = block.z;
        }
        else if (!forward)
        {
            double size = this.entities.size();

            for (Entity entity : this.entities)
            {
                x += Interpolations.lerp(entity.basic.prevPosition.x, entity.basic.position.x, context.transition) / size;
                y += Interpolations.lerp(entity.basic.prevPosition.y, entity.basic.position.y, context.transition) / size;
                z += Interpolations.lerp(entity.basic.prevPosition.z, entity.basic.position.z, context.transition) / size;
            }
        }

        Point point = this.offset.get();

        x += point.x;
        y += point.y;
        z += point.z;

        double dX = x - position.point.x;
        double dY = y - position.point.y;
        double dZ = z - position.point.z;

        if (forward)
        {
            dX = position.point.x - this.position.point.x;
            dY = position.point.y - this.position.point.y;
            dZ = position.point.z - this.position.point.z;
        }

        Angle angle = Angle.angle(dX, dY, dZ);

        float yaw = angle.yaw;
        float pitch = angle.pitch;

        if (this.relative.get() && !forward)
        {
            yaw += position.angle.yaw - this.position.angle.yaw;
            pitch += position.angle.pitch - this.position.angle.pitch;
        }

        position.angle.set(yaw, pitch);
    }

    @Override
    public Clip create()
    {
        return new LookClip();
    }
}