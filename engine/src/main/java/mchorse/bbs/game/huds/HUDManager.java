package mchorse.bbs.game.huds;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.manager.BaseManager;

import java.io.File;

public class HUDManager extends BaseManager<HUDScene>
{
    public HUDManager(File folder)
    {
        super(folder);
    }

    @Override
    protected HUDScene createData(String id, MapType data)
    {
        HUDScene scene = new HUDScene();

        if (data != null)
        {
            scene.fromData(data);
        }

        return scene;
    }
}