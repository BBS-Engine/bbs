package mchorse.bbs.particles.components.motion;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.Operation;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.components.IComponentParticleUpdate;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.Axis;
import org.joml.Vector3d;

import java.util.List;

public class ParticleComponentMotionCollision extends ParticleComponentBase implements IComponentParticleUpdate
{
    public MolangExpression enabled = MolangParser.ONE;
    public float collisionDrag = 0;
    public float bounciness = 1;
    public float radius = 0.01F;
    public boolean expireOnImpact;

    /* Runtime options */
    private Vector3d previous = new Vector3d();
    private Vector3d current = new Vector3d();

    @Override
    public BaseType toData()
    {
        MapType object = new MapType();

        if (MolangExpression.isZero(this.enabled))
        {
            return object;
        }

        if (!MolangExpression.isOne(this.enabled)) object.put("enabled", this.enabled.toData());
        if (this.collisionDrag != 0) object.putFloat("collision_drag", this.collisionDrag);
        if (this.bounciness != 1) object.putFloat("coefficient_of_restitution", this.bounciness);
        if (this.radius != 0.01F) object.putFloat("collision_radius", this.radius);
        if (this.expireOnImpact) object.putBool("expire_on_contact", true);

        return object;
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has("enabled")) this.enabled = parser.parseData(map.get("enabled"));
        if (map.has("collision_drag")) this.collisionDrag = map.getFloat("collision_drag");
        if (map.has("coefficient_of_restitution")) this.bounciness = map.getFloat("coefficient_of_restitution");
        if (map.has("collision_radius")) this.radius = map.getFloat("collision_radius");
        if (map.has("expire_on_contact")) this.expireOnImpact = map.getBool("expire_on_contact");

        return super.fromData(map, parser);
    }

    @Override
    public void update(ParticleEmitter emitter, Particle particle)
    {
        if (emitter.world == null)
        {
            return;
        }

        if (!particle.manual && !Operation.equals(this.enabled.get(), 0))
        {
            float r = this.radius;

            this.previous.set(particle.getGlobalPosition(emitter, particle.prevPosition));
            this.current.set(particle.getGlobalPosition(emitter));

            Vector3d prev = this.previous;
            Vector3d now = this.current;

            double x = now.x - prev.x;
            double y = now.y - prev.y;
            double z = now.z - prev.z;
            boolean veryBig = Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10;

            if (veryBig || emitter.world.chunks.getCell((int) now.x, (int) now.y, (int) now.z, false) == null)
            {
                return;
            }

            AABB aabb = AABB.fromTwoPoints(prev.x - r, prev.y - r, prev.z - r, prev.x + r, prev.y + r, prev.z + r);

            double originalY = y;
            double originalX = x;
            double originalZ = z;

            List<AABB> list = emitter.world.getCollisionAABBs(aabb.copy().expand(x, y, z));

            for (AABB collisionBox : list)
            {
                y = collisionBox.calculateOffset(Axis.Y, aabb, y);
            }

            aabb.offset(0, y, 0);

            for (AABB collisionBox : list)
            {
                x = collisionBox.calculateOffset(Axis.X, aabb, x);
            }

            aabb.offset(x, 0, 0);

            for (AABB collisionBox : list)
            {
                z = collisionBox.calculateOffset(Axis.Z, aabb, z);
            }

            aabb.offset(0, 0, z);

            if (originalY != y || originalX != x || originalZ != z)
            {
                if (this.expireOnImpact)
                {
                    particle.dead = true;

                    return;
                }

                if (particle.relativePosition)
                {
                    particle.relativePosition = false;
                    particle.prevPosition.set(prev);
                }

                now.set(aabb.x + r, aabb.y + r, aabb.z + r);

                if (originalY != y)
                {
                    particle.accelerationFactor.y *= -this.bounciness;
                    now.y += originalY < y ? r : -r;
                }

                if (originalX != x)
                {
                    particle.accelerationFactor.x *= -this.bounciness;
                    now.x += originalX < x ? r : -r;
                }

                if (originalZ != z)
                {
                    particle.accelerationFactor.z *= -this.bounciness;
                    now.z += originalZ < z ? r : -r;
                }

                particle.position.set(now);
                particle.dragFactor += this.collisionDrag;
            }
        }
    }

    @Override
    public int getSortingIndex()
    {
        return 50;
    }
}