package mchorse.bbs.particles;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.manager.BaseManager;
import mchorse.bbs.game.utils.manager.storage.JSONLikeStorage;

import java.io.File;

public class ParticleManager extends BaseManager<ParticleScheme>
{
    public ParticleManager(File folder)
    {
        super(folder);

        this.storage = new JSONLikeStorage().json();
    }

    @Override
    protected ParticleScheme createData(String id, MapType data)
    {
        ParticleScheme scheme = new ParticleScheme();

        if (data != null)
        {
            try
            {
                ParticleScheme.PARSER.fromData(scheme, data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            scheme.setup();
        }

        return scheme;
    }
}