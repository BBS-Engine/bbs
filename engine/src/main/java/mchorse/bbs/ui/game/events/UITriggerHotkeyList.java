package mchorse.bbs.ui.game.events;

import mchorse.bbs.game.misc.hotkeys.TriggerHotkey;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.utils.keys.KeyCodes;

import java.util.List;
import java.util.function.Consumer;

public class UITriggerHotkeyList extends UIList<TriggerHotkey>
{
    public UITriggerHotkeyList(Consumer<List<TriggerHotkey>> callback)
    {
        super(callback);
    }

    @Override
    protected String elementToString(int i, TriggerHotkey element)
    {
        return KeyCodes.getName(element.keycode);
    }
}