package mchorse.bbs.game.scripts.user.global;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;

/**
 * Script item API.
 *
 * <p>You can access it in the script as <code>bbs.data</code> or <code>bbs.getData()</code>.
 */
public interface IScriptData
{
    /**
     * Create an empty data map.
     *
     * <pre>{@code
     *    var map = bbs.data.map();
     *
     *    map.putString("id", "bbs:model");
     *    map.putString("bbs:model", "normie");
     *
     *    var form = bbs.forms.create(map);
     *
     *    bbs.send(map.toString()); // {id:"bbs:model",model:"normie"}
     *    bbs.send(form.getId());
     * }</pre>
     */
    public default MapType map()
    {
        return this.map(null);
    }

    /**
     * Parse a map data out of given string, if string data was
     * invalid then an empty map will be returned.
     *
     * <pre>{@code
     *    var map = bbs.data.map("{id:\"bbs:model\",model:\"normie\"}");
     *
     *    bbs.send(map.toString()); // {id:"bbs:model",model:"normie"}
     * }</pre>
     */
    public MapType map(String string);

    /**
     * Turn a JS object into a data map.
     *
     * <pre>{@code
     *    var map = bbs.data.mapFromJS({
     *        id: "bbs:model",
     *        model: "normie"
     *    });
     *
     *    bbs.send(map.toString()); // {id:"bbs:model",model:"normie"}
     * }</pre>
     */
    public MapType mapFromJS(Object jsObject);

    /**
     * Create an empty list.
     *
     * <pre>{@code
     *    var list = bbs.data.list();
     *
     *    list.addInt(1);
     *    list.addInt(2);
     *    list.addInt(3);
     *    list.addInt(4);
     *    list.addInt(5);
     *    list.addInt(6);
     *
     *    // [1,2,3,4,5,6]
     *    bbs.send(list.toString());
     * }</pre>
     */
    public default ListType list()
    {
        return this.list(null);
    }

    /**
     * Parse a list data out of given string, if data string was
     * invalid then an empty list will be returned.
     *
     * <pre>{@code
     *    var list = bbs.data.list("[1, 2, 3, 4, 5, 6]");
     *
     *    // [1,2,3,4,5,6]
     *    bbs.send(list.toString());
     * }</pre>
     *
     * @param string
     */
    public ListType list(String string);

    /**
     * Turn a JS object into a list data.
     *
     * <pre>{@code
     *    var list = bbs.data.listFromJS([1, 2, 3, 4, 5, 6]);
     *
     *    // [1,2,3,4,5,6]
     *    bbs.send(list.toString());
     * }</pre>
     */
    public ListType listFromJS(Object jsObject);
}
