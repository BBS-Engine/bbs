package mchorse.sandbox.ui.inventory;

public interface IUIInventoryHandler
{
    public void hold(IUIInventory element, int index);

    public default boolean isHolding()
    {
        return this.getCurrentHolder() != null;
    }

    public IUIInventory getCurrentHolder();

    public int getCurrentIndex();

    public void requestItem(IUIInventory inventory, int index);
}