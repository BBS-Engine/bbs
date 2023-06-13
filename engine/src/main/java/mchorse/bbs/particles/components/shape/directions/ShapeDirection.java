package mchorse.bbs.particles.components.shape.directions;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.particles.emitter.Particle;

public abstract class ShapeDirection
{
    public abstract void applyDirection(Particle particle, double x, double y, double z);

    public abstract BaseType toData();
}