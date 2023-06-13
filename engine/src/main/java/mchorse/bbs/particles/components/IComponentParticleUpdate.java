package mchorse.bbs.particles.components;

import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.particles.emitter.Particle;

public interface IComponentParticleUpdate extends IComponentBase
{
    public void update(ParticleEmitter emitter, Particle particle);
}