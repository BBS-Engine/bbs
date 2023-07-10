package mchorse.bbs.camera;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.manager.BaseManager;

import java.io.File;

public class CameraManager extends BaseManager<CameraWork>
{
    public CameraManager(File folder)
    {
        super(folder);
    }

    @Override
    protected CameraWork createData(String id, MapType mapType)
    {
        CameraWork work = new CameraWork();

        if (mapType != null)
        {
            work.fromData(mapType);
        }

        return work;
    }
}