package mchorse.bbs.particles.components;

import mchorse.bbs.particles.emitter.ParticleEmitter;

public interface IComponentEmitterUpdate extends IComponentBase
{
    public void update(ParticleEmitter emitter);
}