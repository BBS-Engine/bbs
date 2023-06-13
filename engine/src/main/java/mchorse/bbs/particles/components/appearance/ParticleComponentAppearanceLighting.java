package mchorse.bbs.particles.components.appearance;

import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.components.IComponentEmitterInitialize;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentAppearanceLighting extends ParticleComponentBase implements IComponentEmitterInitialize
{
    @Override
    public void apply(ParticleEmitter emitter)
    {
        emitter.lit = false;
    }

    @Override
    public boolean canBeEmpty()
    {
        return true;
    }
}