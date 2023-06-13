package mchorse.bbs.events.register;

import mchorse.bbs.game.items.ItemManager;

public class RegisterItemsEvent
{
    public ItemManager items;

    public RegisterItemsEvent(ItemManager items)
    {
        this.items = items;
    }
}
