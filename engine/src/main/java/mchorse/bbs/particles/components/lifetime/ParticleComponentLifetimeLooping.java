package mchorse.bbs.particles.components.lifetime;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentLifetimeLooping extends ParticleComponentLifetime
{
    public MolangExpression sleepTime = MolangParser.ZERO;

    @Override
    protected void toData(MapType data)
    {
        super.toData(data);

        if (!MolangExpression.isZero(this.sleepTime))
        {
            data.put("sleep_time", this.sleepTime.toData());
        }
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType element = data.asMap();

        if (element.has("sleep_time"))
        {
            this.sleepTime = parser.parseData(element.get("sleep_time"));
        }

        return super.fromData(element, parser);
    }

    @Override
    public void update(ParticleEmitter emitter)
    {
        double active = this.activeTime.get();
        double sleep = this.sleepTime.get();
        double age = emitter.getAge();

        emitter.lifetime = (int) (active * 20);

        if (age >= active && emitter.playing)
        {
            emitter.stop();
        }

        if (age >= sleep && !emitter.playing)
        {
            emitter.start();
        }
    }
}