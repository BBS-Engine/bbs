package mchorse.bbs.particles.components.shape;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.ParticleUtils;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentShapeBox extends ParticleComponentShapeBase
{
    public MolangExpression[] halfDimensions = {MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO};

    @Override
    protected void toData(MapType data)
    {
        super.toData(data);

        data.put("half_dimensions", ParticleUtils.vectorToList(this.halfDimensions));
    }

    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has("half_dimensions"))
        {
            ParticleUtils.vectorFromList(map.getList("half_dimensions"), this.halfDimensions, parser);
        }

        return super.fromData(map, parser);
    }

    @Override
    public void apply(ParticleEmitter emitter, Particle particle)
    {
        float centerX = (float) this.offset[0].get();
        float centerY = (float) this.offset[1].get();
        float centerZ = (float) this.offset[2].get();

        float w = (float) this.halfDimensions[0].get();
        float h = (float) this.halfDimensions[1].get();
        float d = (float) this.halfDimensions[2].get();

        particle.position.x = centerX + ((float) Math.random() * 2 - 1F) * w;
        particle.position.y = centerY + ((float) Math.random() * 2 - 1F) * h;
        particle.position.z = centerZ + ((float) Math.random() * 2 - 1F) * d;

        if (this.surface)
        {
            int roll = (int) (Math.random() * 6 * 100) % 6;

            if (roll == 0) particle.position.x = centerX + w;
            else if (roll == 1) particle.position.x = centerX - w;
            else if (roll == 2) particle.position.y = centerY + h;
            else if (roll == 3) particle.position.y = centerY - h;
            else if (roll == 4) particle.position.z = centerZ + d;
            else if (roll == 5) particle.position.z = centerZ - d;
        }

        this.direction.applyDirection(particle, centerX, centerY, centerZ);
    }
}