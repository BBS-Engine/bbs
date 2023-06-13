package mchorse.bbs.particles.components.shape.directions;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.particles.emitter.Particle;
import org.joml.Vector3d;

public class ShapeDirectionInwards extends ShapeDirection
{
    public static final ShapeDirection INWARDS = new ShapeDirectionInwards(-1);
    public static final ShapeDirection OUTWARDS = new ShapeDirectionInwards(1);

    private float factor;

    public ShapeDirectionInwards(float factor)
    {
        this.factor = factor;
    }

    public static ShapeDirection fromString(String value)
    {
        if (value.equals("inwards"))
        {
            return INWARDS;
        }

        return OUTWARDS;
    }

    @Override
    public void applyDirection(Particle particle, double x, double y, double z)
    {
        Vector3d vector = new Vector3d(particle.position);

        vector.sub(new Vector3d(x, y, z));

        if (vector.length() <= 0)
        {
            vector.set(0, 0, 0);
        }
        else
        {
            vector.normalize();
            vector.mul(this.factor);
        }

        particle.speed.set(vector);
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.factor < 0 ? "inwards" : "outwards");
    }
}