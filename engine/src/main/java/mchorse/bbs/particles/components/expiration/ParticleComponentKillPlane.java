package mchorse.bbs.particles.components.expiration;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.math.Operation;
import mchorse.bbs.math.molang.MolangException;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.particles.components.IComponentParticleUpdate;
import mchorse.bbs.particles.components.ParticleComponentBase;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import org.joml.Vector3d;

public class ParticleComponentKillPlane extends ParticleComponentBase implements IComponentParticleUpdate
{
    public float a;
    public float b;
    public float c;
    public float d;

    @Override
    public BaseType toData()
    {
        ListType list = new ListType();

        if (Operation.equals(this.a, 0) && Operation.equals(this.b, 0) && Operation.equals(this.c, 0) && Operation.equals(this.d, 0))
        {
            return list;
        }

        list.addFloat(this.a);
        list.addFloat(this.b);
        list.addFloat(this.c);
        list.addFloat(this.d);

        return list;
    }

    @Override
    public ParticleComponentBase fromData(BaseType data, MolangParser parser) throws MolangException
    {
        if (!data.isList())
        {
            return super.fromData(data, parser);
        }

        ListType list = data.asList();

        if (list.size() >= 4)
        {
            this.a = list.getFloat(0);
            this.b = list.getFloat(1);
            this.c = list.getFloat(2);
            this.d = list.getFloat(3);
        }

        return super.fromData(data, parser);
    }

    @Override
    public void update(ParticleEmitter emitter, Particle particle)
    {
        if (particle.dead)
        {
            return;
        }

        Vector3d prevLocal = new Vector3d(particle.prevPosition);
        Vector3d local = new Vector3d(particle.position);

        if (!particle.relativePosition)
        {
            local.sub(emitter.lastGlobal);
            prevLocal.sub(emitter.lastGlobal);
        }

        double prev = this.a * prevLocal.x + this.b * prevLocal.y + this.c * prevLocal.z + this.d;
        double now = this.a * local.x + this.b * local.y + this.c * local.z + this.d;

        if ((prev > 0 && now < 0) || (prev < 0 && now > 0))
        {
            particle.dead = true;
        }
    }

    @Override
    public int getSortingIndex()
    {
        return 100;
    }
}