package mchorse.bbs.screenplay;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.manager.BaseManager;

import java.io.File;

public class ScreenplayManager extends BaseManager<Screenplay>
{
    public ScreenplayManager(File folder)
    {
        super(folder);
    }

    @Override
    protected Screenplay createData(String id, MapType data)
    {
        Screenplay screenplay = new Screenplay();

        if (data != null)
        {
            screenplay.fromData(data);
        }

        return screenplay;
    }
}