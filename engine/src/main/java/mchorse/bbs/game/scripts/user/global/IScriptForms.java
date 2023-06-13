package mchorse.bbs.game.scripts.user.global;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;

/**
 * Script forms API.
 *
 * <p>You can access it in the script as <code>bbs.forms</code> or <code>bbs.getForms()</code>.
 */
public interface IScriptForms
{
    /**
     * Create a form out of string data.
     *
     * <pre>{@code
     *    var form = bbs.forms.create("{id:\"bbs:model\",model:\"normie\"}");
     *
     *    // Do something with Normie
     * }</pre>
     */
    public Form create(String string);

    /**
     * Create a form out of data.
     *
     * <pre>{@code
     *    var data = bbs.data.map();
     *
     *    data.putString("id", "bbs:model");
     *    data.putString("bbs:model", "normie");
     *
     *    var form = bbs.forms.create(data);
     *
     *    // Assuming c.getSubject() is a player
     *    c.getSubject().setForm(form);
     * }</pre>
     */
    public Form create(MapType map);
}