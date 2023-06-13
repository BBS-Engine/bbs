package mchorse.bbs.game.misc;

import mchorse.bbs.BBS;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.events.register.RegisterGameSettingsEvent;
import mchorse.bbs.game.misc.hotkeys.TriggerHotkeys;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.settings.values.ValueContentType;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global server settings
 */
public class GameSettings implements IMapSerializable
{
    private File file;

    public final Map<String, Trigger> registered = new LinkedHashMap<String, Trigger>();
    public final TriggerHotkeys hotkeys = new TriggerHotkeys();

    /* World triggers */
    public final Trigger worldLoad;
    public final Trigger worldTick;

    /* Player triggers */
    public final Trigger playerItemPickup;
    public final Trigger playerMouseClick;

    /* UI triggers */
    public final Trigger uiMenuOpen;
    public final Trigger uiMenuClose;

    public final ValueGroup settings = new ValueGroup("game");

    public ValueContentType pauseUI = new ValueContentType("pauseUI", ContentType.UIS, "");
    public ValueContentType inventoryUI = new ValueContentType("inventoryUI", ContentType.UIS, "");
    public ValueContentType dialogueUI = new ValueContentType("dialogueUI", ContentType.UIS, "");

    public Trigger register(String key, Trigger trigger)
    {
        if (this.registered.containsKey(key))
        {
            throw new IllegalStateException("Game trigger '" + key + "' is already registered!");
        }

        this.registered.put(key, trigger);

        return trigger;
    }

    public GameSettings(File file)
    {
        this.file = file;

        /* Register default triggers */
        this.worldLoad = this.register("world_load", new Trigger());
        this.worldTick = this.register("world_tick", new Trigger());

        this.playerItemPickup = this.register("player_item_pickup", new Trigger());
        this.playerMouseClick = this.register("player_mouse_click", new Trigger());

        this.uiMenuOpen = this.register("ui_menu_open", new Trigger());
        this.uiMenuClose = this.register("ui_menu_close", new Trigger());

        /* Register default settings */
        this.settings.add(this.pauseUI);
        this.settings.add(this.inventoryUI);
        this.settings.add(this.dialogueUI);

        BBS.events.post(new RegisterGameSettingsEvent(this));
    }

    /* Deserialization / Serialization */

    public void load()
    {
        if (this.file == null || !this.file.isFile())
        {
            return;
        }

        try
        {
            this.fromData((MapType) DataToString.read(this.file));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void save()
    {
        DataToString.writeSilently(this.file, this.toData());
    }

    @Override
    public void toData(MapType data)
    {
        MapType triggers = new MapType();

        for (Map.Entry<String, Trigger> entry : this.registered.entrySet())
        {
            this.writeTrigger(triggers, entry.getKey(), entry.getValue());
        }

        if (!triggers.isEmpty())
        {
            data.put("triggers", triggers);
        }

        data.put("hotkeys", this.hotkeys.toData());

        MapType settings = new MapType();

        for (BaseValue value : this.settings.getAll())
        {
            settings.put(value.getId(), value.toData());
        }

        data.put("settings", settings);
    }

    private void writeTrigger(MapType data, String key, Trigger trigger)
    {
        if (trigger != null)
        {
            MapType triggerMap = trigger.toData();

            if (!triggerMap.isEmpty())
            {
                data.put(key, triggerMap);
            }
        }
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("triggers"))
        {
            MapType triggers = data.getMap("triggers");

            for (Map.Entry<String, Trigger> entry : this.registered.entrySet())
            {
                this.readTrigger(triggers, entry.getKey(), entry.getValue());
            }
        }

        if (data.has("hotkeys"))
        {
            this.hotkeys.fromData(data.getMap("hotkeys"));
        }

        if (data.has("settings"))
        {
            MapType settings = data.getMap("settings");

            for (BaseValue value : this.settings.getAll())
            {
                if (settings.has(value.getId()))
                {
                    value.fromData(settings.get(value.getId()));
                }
            }
        }
    }

    private void readTrigger(MapType data, String key, Trigger trigger)
    {
        if (data.has(key))
        {
            MapType triggerMap = data.getMap(key);

            if (!triggerMap.isEmpty())
            {
                trigger.fromData(triggerMap);
            }
        }
    }
}