package mchorse.bbs.animation;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.manager.BaseManager;

import java.io.File;

public class AnimationManager extends BaseManager<Animation>
{
    public AnimationManager(File folder)
    {
        super(folder);
    }

    @Override
    protected boolean canCache()
    {
        return false;
    }

    @Override
    protected Animation createData(String id, MapType data)
    {
        Animation animation = new Animation();

        if (data != null)
        {
            animation.fromData(data);
        }

        return animation;
    }
}