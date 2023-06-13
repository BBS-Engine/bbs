package mchorse.bbs.particles.components.meta;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.components.IComponentEmitterInitialize;
import mchorse.bbs.particles.components.IComponentEmitterUpdate;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.ParticleEmitter;

public class ParticleComponentInitialization extends ParticleComponentBase implements IComponentEmitterInitialize, IComponentEmitterUpdate
{
    public MolangExpression creation = MolangParser.ZERO;
    public MolangExpression update = MolangParser.ZERO;

    @Override
    protected void toData(MapType data)
    {
        if (!MolangExpression.isZero(this.creation)) data.put("creation_expression", this.creation.toData());
        if (!MolangExpression.isZero(this.update)) data.put("per_update_expression", this.update.toData());
    }

    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isMap())
        {
            return super.fromData(data, parser);
        }

        MapType element = data.asMap();

        if (element.has("creation_expression")) this.creation = parser.parseGlobalData(element.get("creation_expression"));
        if (element.has("per_update_expression")) this.update = parser.parseGlobalData(element.get("per_update_expression"));

        return super.fromData(element, parser);
    }

    @Override
    public void apply(ParticleEmitter emitter)
    {
        this.creation.get();
        emitter.replaceVariables();
    }

    @Override
    public void update(ParticleEmitter emitter)
    {
        this.update.get();
        emitter.replaceVariables();
    }
}