package mchorse.bbs.events.register;

import mchorse.bbs.BBS;
import mchorse.bbs.settings.SettingsBuilder;

import java.util.function.Consumer;

public class RegisterSettingsEvent
{
    public void register(String id, Consumer<SettingsBuilder> consumer)
    {
        BBS.setupConfig(id, BBS.getConfigPath(id + ".json"), consumer);
    }
}