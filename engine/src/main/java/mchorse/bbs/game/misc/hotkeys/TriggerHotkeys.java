package mchorse.bbs.game.misc.hotkeys;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.game.utils.DataContext;

import java.util.ArrayList;
import java.util.List;

public class TriggerHotkeys implements IMapSerializable
{
    public List<TriggerHotkey> hotkeys = new ArrayList<TriggerHotkey>();

    public boolean execute(Entity player, int keycode, boolean down)
    {
        for (TriggerHotkey hotkey : this.hotkeys)
        {
            if (hotkey.canTrigger(keycode, down))
            {
                hotkey.execute(new DataContext(player).set("key", keycode).set("down", down ? 1 : 0));

                return true;
            }
        }

        return false;
    }

    @Override
    public void toData(MapType data)
    {
        ListType hotkeys = new ListType();

        for (TriggerHotkey hotkey : this.hotkeys)
        {
            hotkeys.add(hotkey.toData());
        }

        data.put("hotkeys", hotkeys);
    }

    @Override
    public void fromData(MapType data)
    {
        this.hotkeys.clear();

        if (data.has("hotkeys"))
        {
            ListType hotkeys = data.getList("hotkeys");

            for (int i = 0; i < hotkeys.size(); i++)
            {
                TriggerHotkey hotkey = new TriggerHotkey();

                hotkey.fromData(hotkeys.getMap(i));
                this.hotkeys.add(hotkey);
            }
        }
    }
}