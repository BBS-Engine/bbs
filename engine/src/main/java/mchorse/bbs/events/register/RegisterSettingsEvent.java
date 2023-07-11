package mchorse.bbs.events.register;

import mchorse.bbs.BBS;
import mchorse.bbs.settings.SettingsBuilder;
import mchorse.bbs.ui.utils.icons.Icon;

import java.util.function.Consumer;

public class RegisterSettingsEvent
{
    public void register(Icon icon, String id, Consumer<SettingsBuilder> consumer)
    {
        BBS.setupConfig(icon, id, BBS.getConfigPath(id + ".json"), consumer);
    }
}