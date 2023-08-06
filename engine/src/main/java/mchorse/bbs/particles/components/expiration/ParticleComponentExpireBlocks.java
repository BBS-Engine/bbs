package mchorse.bbs.particles.components.expiration;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public abstract class ParticleComponentExpireBlocks extends ParticleComponentBase
{
    public List<Byte> blocks = new ArrayList<>();

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        for (Byte block : this.blocks)
        {
            list.addString(block.toString());
        }

        return list;
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isList())
        {
            return super.fromData(data, parser);
        }

        for (BaseType value : data.asList())
        {
            try
            {
                this.blocks.add(Byte.parseByte(value.asString()));
            }
            catch (Exception e)
            {}
        }

        return super.fromData(data, parser);
    }

    public IBlockVariant getBlock(ParticleEmitter emitter, Particle particle)
    {
        if (emitter.world == null)
        {
            return null;
        }

        Vector3d position = particle.getGlobalPosition(emitter);

        return emitter.world.chunks.getBlock((int) position.x, (int) position.y, (int) position.z);
    }
}