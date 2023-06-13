package mchorse.bbs.particles.components.shape;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.ParticleUtils;
import mchorse.bbs.particles.components.IComponentParticleInitialize;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.components.shape.directions.ShapeDirection;
import mchorse.bbs.particles.components.shape.directions.ShapeDirectionInwards;
import mchorse.bbs.particles.components.shape.directions.ShapeDirectionVector;

public abstract class ParticleComponentShapeBase extends ParticleComponentBase implements IComponentParticleInitialize
{
    public MolangExpression[] offset = {MolangParser.ZERO, MolangParser.ZERO, MolangParser.ZERO};
    public ShapeDirection direction = ShapeDirectionInwards.OUTWARDS;
    public boolean surface = false;

    @Override
    protected void toData(MapType data)
    {
        data.put("offset", ParticleUtils.vectorToList(this.offset));

        if (this.direction != ShapeDirectionInwards.OUTWARDS)
        {
            data.put("direction", this.direction.toData());
        }

        if (this.surface)
        {
            data.putBool("surface_only", true);
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

        if (map.has("offset"))
        {
            ParticleUtils.vectorFromList(map.getList("offset"), this.offset, parser);
        }

        if (map.has("direction"))
        {
            BaseType direction = map.get("direction");

            if (direction.isString())
            {
                this.direction = ShapeDirectionInwards.fromString(direction.asString());
            }
            else if (direction.isList())
            {
                ListType list = direction.asList();

                if (list.size() >= 3)
                {
                    this.direction = new ShapeDirectionVector(
                        parser.parseData(list.get(0)),
                        parser.parseData(list.get(1)),
                        parser.parseData(list.get(2))
                    );
                }
            }
        }

        if (map.has("surface_only"))
        {
            this.surface = map.getBool("surface_only");
        }

        return super.fromData(map, parser);
    }
}