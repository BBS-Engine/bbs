package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.ITextColoring;

public abstract class UILabelBaseComponent extends UIComponent
{
    public String label = "";
    public int color;
    public boolean textShadow = true;
    public boolean hasBackground = true;

    /**
     * Change text color of this component by providing hex RGB.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IMappetUIContext
     *
     *    // Set label's text color to toxic green
     *    uiContext.get("component").color(0x00ff00);
     * }</pre>
     */
    public UILabelBaseComponent color(int color)
    {
        return this.color(color, true);
    }

    /**
     * Change text color of this component by providing hex RGB.
     * Optionally enable text shadow.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IMappetUIContext
     *
     *    // Set label's text color to black (and without text shadow)
     *    uiContext.get("component").color(0x000000, false);
     * }</pre>
     */
    public UILabelBaseComponent color(int color, boolean shadow)
    {
        this.change("color", "textShadow");

        this.color = color;
        this.textShadow = shadow;

        return this;
    }

    /**
     * Set label for label, toggle and text UI components, or change
     * the input value for textbox and textarea components.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IMappetUIContext
     *
     *    // Assuming that title is a label UI component
     *    uiContext.get("title").label("Application form");
     *
     *    // Assuming that prerequisites is a text UI component
     *    uiContext.get("prerequisites").label("This is an application form for enrolling into H.P. Lovecraft's book club.\n\n* - are required fields");
     *
     *    // Assuming that fullname is a textbox UI component
     *    uiContext.get("fullname").label("John Smith");
     *
     *    // Assuming that description is a textarea UI component
     *    uiContext.get("description").label("I'm John Smith, I'm from Alaska, and I like fishing.");
     *
     *    // Assuming that adult is a toggle UI component
     *    uiContext.get("adult").label("Adult");
     * }</pre>
     */
    public UILabelBaseComponent label(String label)
    {
        this.change("label");

        this.label = label;

        return this;
    }

    /**
     * Disable textbox's background.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("textbox").noBackground();
     * }</pre>
     */
    public UILabelBaseComponent noBackground()
    {
        this.change("hasBackground");

        this.hasBackground = false;

        return this;
    }

    @DiscardMethod
    protected String getLabel()
    {
        return TextUtils.processColoredText(this.label);
    }

    @Override
    protected UIElement apply(UIElement element, UserInterfaceContext context)
    {
        if (element instanceof ITextColoring && this.color != 0)
        {
            ((ITextColoring) element).setColor(this.color, this.textShadow);
        }

        return super.apply(element, context);
    }

    @Override
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        if (key.equals("color") && element instanceof ITextColoring)
        {
            ((ITextColoring) element).setColor(this.color, this.textShadow);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("label", this.label);

        if (this.color != 0)
        {
            data.putInt("color", this.color);
        }

        data.putBool("textShadow", this.textShadow);
        data.putBool("hasBackground", this.hasBackground);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("label"))
        {
            this.label = data.getString("label");
        }

        if (data.has("color"))
        {
            this.color = data.getInt("color");
        }

        if (data.has("textShadow"))
        {
            this.textShadow = data.getBool("textShadow");
        }

        if (data.has("hasBackground"))
        {
            this.hasBackground = data.getBool("hasBackground");
        }
    }
}