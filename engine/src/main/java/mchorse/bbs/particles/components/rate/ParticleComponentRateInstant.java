package mchorse.bbs.particles.components.rate;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.Operation;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.math.molang.expressions.MolangValue;
import mchorse.bbs.particles.components.IComponentEmitterUpdate;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentRateInstant extends ParticleComponentRate implements IComponentEmitterUpdate
{
    public static final MolangExpression DEFAULT_PARTICLES = new MolangValue(null, new Constant(10));

    public ParticleComponentRateInstant()
    {
        this.particles = DEFAULT_PARTICLES;
    }

    @Override
    protected void toData(MapType data)
    {
        if (!MolangExpression.isConstant(this.particles, 10))
        {
            data.put("num_particles", this.particles.toData());
        }
    }

    public ParticleComponentBase fromData(BaseType elem, MolangParser parser) throws MolangException
    {
        if (!elem.isMap())
        {
            return super.fromData(elem, parser);
        }

        MapType map = elem.asMap();

        if (map.has("num_particles"))
        {
            this.particles = parser.parseData(map.get("num_particles"));
        }

        return super.fromData(map, parser);
    }

    @Override
    public void update(ParticleEmitter emitter)
    {
        double age = emitter.getAge();

        if (emitter.playing && Operation.equals(age, 0))
        {
            emitter.setEmitterVariables(0);

            for (int i = 0, c = (int) this.particles.get(); i < c; i ++)
            {
                emitter.spawnParticle();
            }
        }
    }
}