package mchorse.app.ui.inventory;

import mchorse.bbs.game.items.ItemInventory;
import mchorse.bbs.game.items.ItemStack;

public interface IUIInventory
{
    public void accept(int slot, ItemStack stack);

    public ItemInventory getInventory();
}