package mchorse.bbs.particles.components.lifetime;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.math.molang.expressions.MolangValue;
import mchorse.bbs.particles.components.IComponentEmitterUpdate;
import mchorse.bbs.particles.components.ParticleComponentBase;

public abstract class ParticleComponentLifetime extends ParticleComponentBase implements IComponentEmitterUpdate
{
    public static final MolangExpression DEFAULT_ACTIVE = new MolangValue(null, new Constant(10));

    public MolangExpression activeTime = DEFAULT_ACTIVE;

    @Override
    protected void toData(MapType data)
    {
        if (!MolangExpression.isConstant(this.activeTime, 10))
        {
            data.put(this.getPropertyName(), this.activeTime.toData());
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

        if (map.has(this.getPropertyName()))
        {
            this.activeTime = parser.parseData(map.get(this.getPropertyName()));
        }

        return super.fromData(map, parser);
    }

    protected String getPropertyName()
    {
        return "active_time";
    }

    @Override
    public int getSortingIndex()
    {
        return -10;
    }
}