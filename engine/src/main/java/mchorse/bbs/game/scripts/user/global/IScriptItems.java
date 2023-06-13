package mchorse.bbs.game.scripts.user.global;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.items.ItemStack;

/**
 * Script item API.
 *
 * <p>You can access it in the script as <code>bbs.items</code> or <code>bbs.getItems()</code>.
 */
public interface IScriptItems
{
    /**
     * Create an item stack out of data.
     *
     * <pre>{@code
     *    var data = bbs.data.map("{id:\"bbs@hamster\",size:2}");
     *    var item = bbs.items.create(data);
     *
     *    bbs.send(item.toData()); // {id:"bbs@hamster",size:2}
     * }</pre>
     *
     * @return an item stack from the data, or an empty item stack if the
     *         data doesn't have a valid reference to an existing item
     */
    public ItemStack create(MapType data);

    /**
     * Create an item stack with item ID.
     *
     * <pre>{@code
     *    var item = bbs.items.create("bbs@hamster");
     *
     *    bbs.send(item.toData()); // {id:"bbs@hamster",size:1}
     * }</pre>
     *
     * @return an item stack with an item specified by ID, or an empty item
     *         stack if the block doesn't exist
     */
    public default ItemStack create(String itemId)
    {
        return this.create(itemId, 1);
    }

    /**
     * Create an item stack with item ID, count
     *
     * <pre>{@code
     *    var item = bbs.items.create("bbs@hamster", 99);
     *
     *    bbs.send(item.toData()); // {id:"bbs@hamster",size:99}
     * }</pre>
     *
     * @return an item stack with an item specified by ID, or an empty item
     *         stack if the block doesn't exist
     */
    public ItemStack create(String itemId, int count);

    /**
     * Create an item stack out of string data.
     *
     * <pre>{@code
     *    var stack = bbs.items.createData("{id:\"bbs@hamster\",size:1}");
     *
     *    bbs.send(stack.item.id.toString()); // bbs@hamster
     * }</pre>
     *
     * @return an item stack from the string data, or an empty item stack
     *         if the data doesn't have a valid reference to an existing item
     */
    public ItemStack createData(String string);
}