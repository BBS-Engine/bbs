package mchorse.bbs.particles.components.motion;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.ParticleUtils;
import mchorse.bbs.particles.components.IComponentParticleInitialize;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentInitialSpeed extends ParticleComponentBase implements IComponentParticleInitialize
{
    public MolangExpression speed = MolangParser.ONE;
    public MolangExpression[] direction;

    @Override
    public BaseType toData()
    {
        if (this.direction != null)
        {
            return ParticleUtils.vectorToList(this.direction);
        }

        return this.speed.toData();
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (data.isList())
        {
            this.direction = new MolangExpression[] {MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO};

            ParticleUtils.vectorFromList(data.asList(), this.direction, parser);
        }
        else if (BaseType.isPrimitive(data))
        {
            this.speed = parser.parseData(data);
        }

        return super.fromData(data, parser);
    }

    @Override
    public boolean canBeEmpty()
    {
        return true;
    }

    @Override
    public void apply(ParticleEmitter emitter, Particle particle)
    {
        if (this.direction != null)
        {
            particle.speed.set(
                (float) this.direction[0].get(),
                (float) this.direction[1].get(),
                (float) this.direction[2].get()
            );
        }
        else
        {
            float speed = (float) this.speed.get();

            particle.speed.mul(speed);
        }
    }

    @Override
    public int getSortingIndex()
    {
        return 5;
    }
}