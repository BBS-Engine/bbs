package mchorse.bbs.particles.components;

import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.particles.emitter.Particle;

public interface IComponentParticleInitialize extends IComponentBase
{
    public void apply(ParticleEmitter emitter, Particle particle);
}