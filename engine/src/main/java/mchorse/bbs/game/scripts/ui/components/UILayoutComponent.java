package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.ui.utils.LayoutType;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.game.utils.EnumUtils;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.resizers.layout.ColumnResizer;
import mchorse.bbs.ui.utils.resizers.layout.GridResizer;
import mchorse.bbs.ui.utils.resizers.layout.RowResizer;

/**
 * Layout UI component.
 *
 * <p>This UI component does nothing, beside managing placement of other
 * components. Layout UI component have three different modes upon which
 * child components can be placed: column, row, and grid.</p>
 *
 * <p>Additionally, column mode supports scrolling when there are too many
 * components within its frame.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#layout()},
 * {@link IScriptUIBuilder#column(int)}, {@link IScriptUIBuilder#row(int)}, and
 * {@link IScriptUIBuilder#grid(int)} methods.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create().background();
 *        var column = ui.column(4, 10);
 *
 *        column.getCurrent().scroll().rxy(0.5, 0.5).w(240).rh(0.8).anchor(0.5);
 *
 *        var row = column.row(5);
 *        var name = row.column(4);
 *
 *        name.label("Name").h(8);
 *        name.textbox().id("name").h(20);
 *
 *        var lastname = row.column(4);
 *
 *        lastname.label("Last name").h(8);
 *        lastname.textbox().id("lastname").h(20);
 *
 *        column.toggle("I agree to ToS").id("toggle").h(14);
 *        column.text("The terms of service are following: you agree that your data will be used by an AI to generate funny cat and dog videos based entirely on your name and lastname.\n\nYou also agree to give us your time to view those videos, because we said so.").color(0xaaaaaa, false).marginTop(8);
 *        column.button("Oh... a button?").h(20).marginTop(12);
 *
 *        bbs.ui.open(ui);
 *    }
 * }</pre>
 */
public class UILayoutComponent extends UIParentComponent
{
    public boolean scroll;
    public int scrollSize;
    public boolean horizontal;

    public LayoutType layoutType = LayoutType.NONE;
    public int margin;
    public int padding;

    public int width;
    public int items;

    /**
     * Enables scrolling. This option works only with {@link IScriptUIBuilder#column(int)}
     * layout component.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create().background();
     *        var column = ui.column(4, 10);
     *
     *        column.getCurrent().scroll().rxy(0.5, 0.5).wh(200, 200).anchor(0.5);
     *
     *        column.label("Name").h(8);
     *        column.textbox().id("name").h(20);
     *        column.label("Last name").h(8);
     *        column.textbox().id("lastname").h(20);
     *
     *        column.toggle("I agree to ToS").id("toggle").h(14);
     *        column.text("The terms of service are following: you agree that your data will be used by an AI to generate funny cat and dog videos based entirely on your name and lastname.\n\nYou also agree to give us your time to view those videos, because we said so.").color(0xaaaaaa, false).marginTop(8);
     *
     *        for (var i = 0; i < 10; i++)
     *        {
     *            column.button("Button " + (i + 1)).h(20);
     *        }
     *
     *        column.text("These 10 buttons above demonstrate the ability of this layout element to scroll down.").marginTop(12);
     *
     *        bbs.ui.open(ui);
     *    }
     * }</pre>
     */
    public UILayoutComponent scroll()
    {
        this.scroll = true;

        return this;
    }

    /**
     * Set manually scroll size of the layout element. This works only with
     * basic {@link IScriptUIBuilder#layout()} component.
     *
     * <p>If {@link UILayoutComponent#horizontal()} was enabled earlier,
     * then this value will change the max scrollable to width, rather than
     * height.</p>
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var size = 400;
     *
     *        var ui = bbs.ui.create().background();
     *
     *        // Demonstration of manual vertical scroll area
     *        var vertical = ui.layout();
     *
     *        vertical.getCurrent().scroll().scrollSize(size).rxy(0.25, 0.5).wh(150, 200).anchor(0.5);
     *        vertical.button("Top left").xy(10, 10).wh(100, 20);
     *        vertical.button("Middle").rx(0.5, -50).y(size / 2 - 10).wh(100, 20);
     *        vertical.button("Bottom right").rx(1, -110).y(size - 30).wh(100, 20);
     *
     *        ui.label("Vertical scroll").background(0x88000000).rx(0.25).ry(0.5, -120).wh(100, 20).anchorX(0.5).labelAnchor(0.5, 0);
     *
     *        // Demonstration of manual horizontal scroll area
     *        var horizontal = ui.layout();
     *
     *        horizontal.getCurrent().scroll().horizontal().scrollSize(size).rxy(0.75, 0.5).wh(150, 200).anchor(0.5);
     *        horizontal.button("Top left").xy(10, 10).wh(100, 20);
     *        horizontal.button("Middle").x(size / 2 - 50).ry(0.5, -10).wh(100, 20);
     *        horizontal.button("Bottom right").x(size - 110).ry(1, -30).wh(100, 20);
     *
     *        ui.label("Horizontal scroll").background(0x88000000).rx(0.75).ry(0.5, -120).wh(100, 20).anchorX(0.5).labelAnchor(0.5, 0);
     *
     *        bbs.ui.open(ui);
     *    }
     * }</pre>
     */
    public UILayoutComponent scrollSize(int scrollSize)
    {
        this.change("scrollSize");

        this.scrollSize = scrollSize;

        return this;
    }

    /**
     * Enables horizontal mode. This usable when {@link UILayoutComponent#scroll()}
     * is enabled.
     */
    public UILayoutComponent horizontal()
    {
        this.horizontal = true;

        return this;
    }

    /**
     * Per component width (in pixels) that should be sustained within
     * {@link IScriptUIBuilder#grid(int)} layout type. This doesn't work with any other
     * component than grid.
     */
    public UILayoutComponent width(int width)
    {
        this.width = width;

        return this;
    }

    /**
     * How many components per row that should be placed within
     * {@link IScriptUIBuilder#grid(int)} layout type. This option doesn't work with
     * any other component than grid.
     */
    public UILayoutComponent items(int items)
    {
        this.items = items;

        return this;
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UIElement element;

        if (this.scroll)
        {
            UIScrollView scroll = new UIScrollView(this.horizontal ? ScrollDirection.HORIZONTAL : ScrollDirection.VERTICAL);

            if (this.scrollSize > 0)
            {
                scroll.scroll.scrollSize = this.scrollSize;
            }

            element = scroll;
        }
        else
        {
            element = new UIElement();
        }

        for (UIComponent component : this.getChildComponents())
        {
            UIElement created = component.create(context);

            created.relative(element);
            element.add(created);
        }

        if (this.layoutType != null)
        {
            this.applyLayout(element, this.layoutType);
        }

        return this.apply(element, context);
    }

    @DiscardMethod
    private void applyLayout(UIElement element, LayoutType type)
    {
        if (type == LayoutType.COLUMN)
        {
            ColumnResizer column = element.column(this.margin);

            if (this.scroll)
            {
                column.scroll();
            }

            if (!this.horizontal)
            {
                column.vertical();
            }

            if (this.width > 0)
            {
                column.width(this.width);
            }
            else
            {
                column.stretch();
            }

            column.padding(this.padding);
        }
        else if (type == LayoutType.ROW)
        {
            RowResizer row = element.row(this.margin);

            if (this.width > 0)
            {
                row.width(this.width);
            }

            row.padding(this.padding);
        }
        else if (type == LayoutType.GRID)
        {
            GridResizer grid = element.grid(this.margin);

            if (this.width > 0)
            {
                grid.width(this.width);
            }

            if (this.items > 0)
            {
                grid.items(this.items);
            }

            grid.padding(this.padding);
        }
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        if (key.equals("scrollSize") && element instanceof UIScrollView)
        {
            ((UIScrollView) element).scroll.scrollSize = this.scrollSize;
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putBool("scroll", this.scroll);

        if (this.scrollSize > 0)
        {
            data.putInt("scrollSize", this.scrollSize);
        }

        data.putBool("horizontal", this.horizontal);

        if (this.layoutType != null)
        {
            data.putInt("layoutType", this.layoutType.ordinal());
        }

        data.putInt("margin", this.margin);
        data.putInt("padding", this.padding);

        if (this.width > 0)
        {
            data.putInt("width", this.width);
        }

        if (this.items > 0)
        {
            data.putInt("items", this.items);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("scroll")) this.scroll = data.getBool("scroll");
        if (data.has("scrollSize")) this.scrollSize = data.getInt("scrollSize");
        if (data.has("horizontal")) this.horizontal = data.getBool("horizontal");

        if (data.has("layoutType"))
        {
            this.layoutType = EnumUtils.getValue(data.getInt("layoutType"), LayoutType.values(), null);
        }

        if (data.has("margin")) this.margin = data.getInt("margin");
        if (data.has("padding")) this.padding = data.getInt("padding");
        if (data.has("width")) this.width = data.getInt("width");
        if (data.has("items")) this.items = data.getInt("items");
    }
}