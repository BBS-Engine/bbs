package mchorse.bbs.particles.components.expiration;

import mchorse.bbs.particles.components.IComponentParticleUpdate;
import mchorse.bbs.particles.emitter.Particle;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.voxel.blocks.IBlockVariant;

public class ParticleComponentExpireNotInBlocks extends ParticleComponentExpireBlocks implements IComponentParticleUpdate
{
    @Override
    public void update(ParticleEmitter emitter, Particle particle)
    {
        if (particle.dead || emitter.world == null)
        {
            return;
        }

        IBlockVariant current = this.getBlock(emitter, particle);

        for (byte block : this.blocks)
        {
            byte id = current == null ? 0 : (byte) current.getGlobalId();

            if (block == id)
            {
                return;
            }
        }

        particle.dead = true;
    }
}