package mchorse.bbs.particles.components.shape.directions;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.emitter.Particle;

public class ShapeDirectionVector extends ShapeDirection
{
    public MolangExpression x;
    public MolangExpression y;
    public MolangExpression z;

    public ShapeDirectionVector(MolangExpression x, MolangExpression y, MolangExpression z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void applyDirection(Particle particle, double x, double y, double z)
    {
        particle.speed.set((float) this.x.get(), (float) this.y.get(), (float) this.z.get());

        if (particle.speed.length() <= 0)
        {
            particle.speed.set(0, 0, 0);
        }
        else
        {
            particle.speed.normalize();
        }
    }

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        list.add(this.x.toData());
        list.add(this.y.toData());
        list.add(this.z.toData());

        return list;
    }
}