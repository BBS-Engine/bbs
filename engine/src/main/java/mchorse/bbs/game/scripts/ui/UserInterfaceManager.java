package mchorse.bbs.game.scripts.ui;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.manager.BaseManager;

import java.io.File;

public class UserInterfaceManager extends BaseManager<UserInterface>
{
    public UserInterfaceManager(File folder)
    {
        super(folder);
    }

    @Override
    protected UserInterface createData(String id, MapType data)
    {
        UserInterface ui = new UserInterface();

        if (data != null)
        {
            ui.fromData(data);
        }

        return ui;
    }
}