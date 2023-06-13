package mchorse.bbs.game.misc.hotkeys;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.conditions.Condition;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.utils.DataContext;

public class TriggerHotkey implements IMapSerializable
{
    public String title = "";
    public int keycode;
    public boolean toggle;
    public Trigger trigger = new Trigger();
    public Condition enabled = new Condition(true);

    public TriggerHotkey()
    {}

    public TriggerHotkey(int keycode, boolean toggle)
    {
        this.keycode = keycode;
        this.toggle = toggle;
    }

    public void execute(DataContext context)
    {
        if (this.isEnabled(context))
        {
            this.trigger.trigger(context);
        }
    }

    private boolean isEnabled(DataContext context)
    {
        return this.enabled.execute(context);
    }

    public boolean canTrigger(int keycode, boolean down)
    {
        if (this.toggle)
        {
            return this.keycode == keycode;
        }

        return this.keycode == keycode && down;
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("title", this.title);
        data.putInt("keycode", this.keycode);
        data.putBool("toggle", this.toggle);
        data.put("trigger", this.trigger.toData());
        data.put("enabled", this.enabled.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        this.title = data.getString("title");
        this.keycode = data.getInt("keycode");
        this.toggle = data.getBool("toggle");
        this.trigger.fromData(data.getMap("trigger"));
        this.enabled.fromData(data.getMap("enabled"));
    }
}