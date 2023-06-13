package mchorse.bbs.particles.components;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public interface IComponentParticleRender extends IComponentBase
{
    public void preRender(ParticleEmitter emitter, float transition);

    public void render(ParticleEmitter emitter, Particle particle, VAOBuilder builder, float transition);

    public void renderUI(Particle particle, VAOBuilder builder, float transition);

    public void postRender(ParticleEmitter emitter, float transition);
}