package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;

/**
 * Clickable icon button component.
 *
 * <p>This component displays an icon, which allows users to trigger the handler
 * script. The value that gets written to UI context's data (if ID is present)
 * is how many times the icon button was pressed.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#icon(String)} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var button = ui.icon("more").id("icon");
 *
 *        button.rxy(0.5, 0.5).wh(20, 20).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *
 *        if (uiContext.getLast() === "icon")
 *        {
 *            // Get a set of all icons
 *            var icons = Java.type("mchorse.bbs.ui.utils.icons.Icons").ICONS.keySet();
 *
 *            // Set a random icon
 *            var index = Math.floor(Math.random() * icons.size());
 *            var i = 0;
 *
 *            for each (var icon in icons)
 *            {
 *                if (i == index)
 *                {
 *                    uiContext.get("icon").icon(icon);
 *
 *                    break;
 *                }
 *
 *                i += 1;
 *            }
 *        }
 *    }
 * }</pre>
 */
public class UIIconButtonComponent extends UIComponent
{
    public String icon = "";

    public UIIconButtonComponent()
    {}

    /**
     * Change icon component's icon.
     *
     * <p>You can find out all available icons by entering following line into
     * BBS' REPL (it returns a Java Set):</p>
     *
     * <pre>{@code
     *    Java.type("mchorse.bbs.ui.utils.icons.IconRegistry").icons.keySet()
     * }</pre>
     *
     * <p>So using that piece of code, you can get create a GUI that shows
     * every icon with a tooltip:</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create(c, "handler").background();
     *        var icons = Java.type("mchorse.bbs.ui.utils.icons.IconRegistry").icons.keySet();
     *
     *        var grid = ui.grid(5);
     *
     *        grid.getCurrent().width(20).rxy(0.5, 0.5).w(245).anchor(0.5);
     *
     *        for each (var icon in icons)
     *        {
     *            grid.icon(icon).wh(20, 20).tooltip("Icon's ID: " + icon);
     *        }
     *
     *        bbs.ui.open(ui);
     *    }
     * }</pre>
     *
     * <p>A basic example:</p>
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("icon").icon("gear");
     * }</pre>
     *
     * @param icon The icon's ID.
     */
    public UIIconButtonComponent icon(String icon)
    {
        this.change("icon");

        this.icon = icon;

        return this;
    }

    @Override
    @DiscardMethod
    protected boolean isDataReserved()
    {
        return true;
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        UIIcon button = (UIIcon) element;

        if (key.equals("icon"))
        {
            button.both(this.getIcon());
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UIIcon button = new UIIcon(this.getIcon(), (b) ->
        {
            if (!this.id.isEmpty())
            {
                this.populateData(context.data);
                context.dirty(this.id, this.updateDelay);
            }
        });

        return this.apply(button, context);
    }

    @DiscardMethod
    private Icon getIcon()
    {
        Icon icon = Icons.ICONS.get(this.icon);

        if (icon == null)
        {
            icon = Icons.NONE;
        }

        return icon;
    }

    @Override
    @DiscardMethod
    public void populateData(MapType data)
    {
        super.populateData(data);

        if (!this.id.isEmpty())
        {
            data.putInt(this.id, data.getInt(this.id) + 1);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("icon", this.icon);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("icon"))
        {
            this.icon = data.getString("icon");
        }
    }
}