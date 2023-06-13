package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * String list UI component.
 *
 * <p>This component allows users to pick a string out of a list of strings
 * that you provided.</p>
 *
 * <p>The value that gets written to UI context's data (if ID is present) is
 * the selected string that picked in the list.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#stringList(List)} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var strings = ui.stringList(["Apple", "Orange", "Pineapple", "Avocado"]).id("strings").tooltip("Pick a fruit...");
 *        var label = ui.label("...").id("fruit").visible(false);
 *
 *        strings.background(0x88000000).rxy(0.5, 0.5).wh(100, 240).anchor(0.5);
 *        label.rx(0.5).ry(0.5, -160).anchor(0.5, 0.5);
 *        label.background(0x88000000).labelAnchor(0.5, 0.5);
 *
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *        var data = uiContext.getData();
 *
 *        if (uiContext.getLast() === "strings")
 *        {
 *            uiContext.get("fruit").label(data.getString("strings")).visible(true);
 *        }
 *    }
 * }</pre>
 */
public class UIStringListComponent extends UIComponent
{
    public List<String> values = new ArrayList<String>();
    public int selected = -1;
    public int background;

    /**
     * Replace values within this string list.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Replace values in strings
     *    uiContext.get("strings").values("Tomato", "Cucumber", "Pepper", "Cabbage");
     * }</pre>
     */
    public UIStringListComponent values(String... values)
    {
        this.change("values");

        this.values.clear();
        this.values.addAll(Arrays.asList(values));

        return this;
    }

    /**
     * Replace values within this string list.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    var vegetables = ["Tomato", "Cucumber", "Pepper", "Cabbage"];
     *
     *    // Replace values in strings
     *    uiContext.get("strings").values(vegetables);
     * }</pre>
     */
    public UIStringListComponent values(List<String> values)
    {
        this.change("values");

        this.values.clear();
        this.values.addAll(values);

        return this;
    }

    /**
     * Set the currently selected element.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Set first string in the list to be selected
     *    uiContext.get("strings").selected(0);
     * }</pre>
     */
    public UIStringListComponent selected(int selected)
    {
        this.change("selected");

        this.selected = selected;

        return this;
    }

    /**
     * Set component's solid color background.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Set half transparent black background
     *    uiContext.get("strings").background();
     * }</pre>
     */
    public UIStringListComponent background()
    {
        return this.background(Colors.A50);
    }

    /**
     * Set component's solid color background.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Set half transparent toxic green background
     *    uiContext.get("strings").background(0x8800ff00);
     * }</pre>
     */
    public UIStringListComponent background(int background)
    {
        this.change("background");

        this.background = background;

        return this;
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        UIStringList list = (UIStringList) element;

        if (key.equals("values"))
        {
            list.clear();
            list.add(this.values);
        }
        else if (key.equals("selected") && this.selected >= 0)
        {
            list.setIndex(this.selected);
        }
        else if (key.equals("background") && this.background != 0)
        {
            list.background(this.background);
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UIStringList element = new UIStringList((v) ->
        {
            if (!this.id.isEmpty())
            {
                context.data.putString(this.id, v.get(0));
                context.dirty(this.id, this.updateDelay);
            }
        });

        element.add(this.values);

        if (this.selected >= 0)
        {
            element.setIndex(this.selected);
        }

        if (this.background != 0)
        {
            element.background(this.background);
        }

        return this.apply(element, context);
    }

    @Override
    @DiscardMethod
    public void populateData(MapType data)
    {
        super.populateData(data);

        if (!this.id.isEmpty())
        {
            String value = "";

            if (this.selected >= 0 && this.selected < this.values.size())
            {
                value = this.values.get(this.selected);
            }

            data.putString(this.id, value);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        ListType list = new ListType();

        for (String value : this.values)
        {
            list.addString(value);
        }

        if (list.size() > 0)
        {
            data.put("values", list);
        }

        if (this.selected >= 0)
        {
            data.putInt("selected", this.selected);
        }

        if (this.background != 0)
        {
            data.putInt("background", this.background);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("values"))
        {
            ListType list = data.getList("values");

            this.values.clear();

            for (int i = 0, c = list.size(); i < c; i++)
            {
                this.values.add(list.getString(i));
            }
        }

        if (data.has("selected"))
        {
            this.selected = data.getInt("selected");
        }

        if (data.has("background"))
        {
            this.background = data.getInt("background");
        }
    }
}