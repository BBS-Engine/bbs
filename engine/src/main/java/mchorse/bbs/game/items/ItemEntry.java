package mchorse.bbs.game.items;

public class ItemEntry
{
    public Item item;
    public ItemRender render;

    public ItemEntry(Item item)
    {
        this.item = item;
    }

    public ItemEntry(Item item, ItemRender render)
    {
        this.item = item;
        this.render = render;
    }
}