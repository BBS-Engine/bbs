package mchorse.bbs.events.register;

import mchorse.bbs.game.misc.GameSettings;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.resources.Link;

/**
 * Register trigger event. This event can be used to register custom
 * global triggers by mods.
 */
public class RegisterGameSettingsEvent
{
    private final GameSettings settings;

    public RegisterGameSettingsEvent(GameSettings settings)
    {
        this.settings = settings;
    }

    public GameSettings getSettings()
    {
        return this.settings;
    }

    public Trigger registerTrigger(Link id)
    {
        return this.settings.register(id.toString(), new Trigger());
    }
}