package mchorse.bbs.particles.components.meta;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.particles.components.IComponentParticleInitialize;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentLocalSpace extends ParticleComponentBase implements IComponentParticleInitialize
{
    public boolean position;
    public boolean rotation;

    @Override
    protected void toData(MapType data)
    {
        if (this.position) data.putBool("position", true);
        if (this.rotation) data.putBool("rotation", true);
    }

    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has("position")) this.position = map.getBool("position");
        if (map.has("rotation")) this.rotation = map.getBool("rotation");

        return super.fromData(map, parser);
    }

    @Override
    public void apply(ParticleEmitter emitter, Particle particle)
    {
        particle.relativePosition = this.position;
        particle.relativeRotation = this.rotation;

        particle.setupMatrix(emitter);
    }

    @Override
    public int getSortingIndex()
    {
        return 1000;
    }
}