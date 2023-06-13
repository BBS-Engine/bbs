package mchorse.bbs.particles.components.expiration;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.components.IComponentParticleInitialize;
import mchorse.bbs.particles.components.IComponentParticleUpdate;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentParticleLifetime extends ParticleComponentBase implements IComponentParticleInitialize, IComponentParticleUpdate
{
    public MolangExpression expression = MolangParser.ZERO;
    public boolean max;

    @Override
    protected void toData(MapType data)
    {
        data.put(this.max ? "max_lifetime" : "expiration_expression", this.expression.toData());
    }

    @Override
    public ParticleComponentBase fromData(BaseType elem, MolangParser parser) throws MolangException
    {
        if (!elem.isMap())
        {
            return super.fromData(elem, parser);
        }

        MapType element = elem.asMap();
        BaseType expression = null;

        if (element.has("expiration_expression"))
        {
            expression = element.get("expiration_expression");
            this.max = false;
        }
        else if (element.has("max_lifetime"))
        {
            expression = element.get("max_lifetime");
            this.max = true;
        }
        else
        {
            throw new RuntimeException("No expiration_expression or max_lifetime was found in particle_lifetime_expression component");
        }

        this.expression = parser.parseData(expression);

        return super.fromData(element, parser);
    }

    @Override
    public void update(ParticleEmitter emitter, Particle particle)
    {
        if (!this.max && this.expression.get() != 0)
        {
            particle.dead = true;
        }
    }

    @Override
    public void apply(ParticleEmitter emitter, Particle particle)
    {
        if (this.max)
        {
            particle.lifetime = (int) (this.expression.get() * 20);
        }
        else
        {
            particle.lifetime = -1;
        }
    }
}