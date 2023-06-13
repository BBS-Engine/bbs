package mchorse.bbs.particles.components;

import mchorse.bbs.particles.emitter.ParticleEmitter;

public interface IComponentEmitterInitialize extends IComponentBase
{
    public void apply(ParticleEmitter emitter);
}