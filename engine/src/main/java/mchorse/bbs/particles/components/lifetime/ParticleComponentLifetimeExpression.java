package mchorse.bbs.particles.components.lifetime;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.Operation;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentLifetimeExpression extends ParticleComponentLifetime
{
    public MolangExpression expiration = MolangParser.ZERO;

    @Override
    protected void toData(MapType data)
    {
        super.toData(data);

        if (!MolangExpression.isZero(this.expiration))
        {
            data.put("expiration_expression", this.expiration.toData());
        }
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType map = data.asMap();

        if (map.has("expiration_expression"))
        {
            this.expiration = parser.parseData(map.get("expiration_expression"));
        }

        return super.fromData(map, parser);
    }

    @Override
    protected String getPropertyName()
    {
        return "activation_expression";
    }

    @Override
    public void update(ParticleEmitter emitter)
    {
        if (!Operation.equals(this.activeTime.get(), 0))
        {
            emitter.start();
        }

        if (!Operation.equals(this.expiration.get(), 0))
        {
            emitter.stop();
        }
    }
}