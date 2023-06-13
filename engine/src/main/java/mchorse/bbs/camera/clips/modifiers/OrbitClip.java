package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Angle;
import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3f;

/**
 * Orbit modifier
 * 
 * This modifier is responsible for making the camera orbit around
 * the given entity with given yaw and pitch.
 */
public class OrbitClip extends EntityClip
{
    /**
     * In addition, copy yaw and pitch from entity
     */
    public final ValueBoolean copy = new ValueBoolean("copy");

    /**
     * How far away to orbit from the entity
     */
    public final ValueFloat distance = new ValueFloat("distance", 0F);

    /**
     * Yaw to be added to orbit
     */
    public final ValueFloat yaw = new ValueFloat("yaw", 0F);

    /**
     * Pitch to be added to orbit
     */
    public final ValueFloat pitch = new ValueFloat("pitch", 0F);

    public OrbitClip()
    {
        super();

        this.register(this.copy);
        this.register(this.distance);
        this.register(this.yaw);
        this.register(this.pitch);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        if (this.checkForDead())
        {
            this.tryFindingEntity(context.bridge.get(IBridgeWorld.class).getWorld());
        }

        if (this.entities == null)
        {
            return;
        }

        if (!context.applyUnderneath(this.tick.get(), 0, this.position))
        {
            this.position.copy(position);
        }

        float yaw = this.yaw.get() + (position.angle.yaw - this.position.angle.yaw);
        float pitch = this.pitch.get() + (position.angle.pitch - this.position.angle.pitch);
        float distance = this.distance.get() + (float) (position.point.z - this.position.point.z);
        Entity entity = this.entities.get(0);
        Vector3f vector = Matrices.rotation(MathUtils.toRad(pitch), MathUtils.toRad(-yaw));

        if (this.copy.get())
        {
            float entityYaw = MathUtils.toDeg(Interpolations.lerp(entity.basic.prevRotation.y, entity.basic.rotation.y, context.transition));
            float entityPitch = MathUtils.toDeg(Interpolations.lerp(entity.basic.prevRotation.x, entity.basic.rotation.x, context.transition));

            Matrices.rotate(vector, MathUtils.toRad(-entityPitch), MathUtils.toRad(-entityYaw));
        }

        Point offset = this.offset.get();
        double x = Interpolations.lerp(entity.basic.prevPosition.x, entity.basic.position.x, context.transition) + offset.x;
        double y = Interpolations.lerp(entity.basic.prevPosition.y, entity.basic.position.y, context.transition) + offset.y;
        double z = Interpolations.lerp(entity.basic.prevPosition.z, entity.basic.position.z, context.transition) + offset.z;

        vector.mul(distance);

        double fX = x + vector.x;
        double fY = y + vector.y;
        double fZ = z + vector.z;
        Angle angle = Angle.angle(x - fX, y - fY, z - fZ);

        position.point.set(fX, fY, fZ);
        position.angle.set(angle.yaw, angle.pitch);
    }

    @Override
    public Clip create()
    {
        return new OrbitClip();
    }
}