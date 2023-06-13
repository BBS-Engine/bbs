package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;

/**
 * Textbox UI component.
 *
 * <p>This component allows users to input a text line (for multi-line input
 * use {@link UITextareaComponent} component). This could be used to request
 * the player to input names.</p>
 *
 * <p>The value that gets written to UI context's data (if ID is present) is
 * the string that user typed into the component.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#textbox()} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var textbox = ui.textbox().id("textbox").tooltip("Enter your name:");
 *
 *        textbox.rxy(0.5, 0.5).wh(160, 20).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *        var data = uiContext.getData();
 *
 *        if (uiContext.getLast() === "textbox")
 *        {
 *            bbs.send("Your name is: " + data.getString("textbox"));
 *        }
 *    }
 * }</pre>
 */
public class UITextboxComponent extends UILabelBaseComponent
{
    public int maxLength = 40;

    /**
     * Change component's max length (how many character max can be inputted).
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("textbox").maxLength(68);
     * }</pre>
     */
    public UITextboxComponent maxLength(int maxLength)
    {
        this.change("maxLength");

        this.maxLength = maxLength;

        return this;
    }

    @Override
    @DiscardMethod
    protected int getDefaultUpdateDelay()
    {
        return UIComponent.DELAY;
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        if (key.equals("label"))
        {
            ((UITextbox) element).setText(this.label);
        }
        else if (key.equals("maxLength"))
        {
            ((UITextbox) element).textbox.setLength(this.maxLength);
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UITextbox element = new UITextbox(this.maxLength, (t) ->
        {
            if (!this.id.isEmpty())
            {
                context.data.putString(this.id, t);
                context.dirty(this.id, this.updateDelay);
            }
        });

        element.setText(this.label);
        element.background(this.hasBackground);

        return this.apply(element, context);
    }

    @Override
    @DiscardMethod
    public void populateData(MapType data)
    {
        super.populateData(data);

        if (!this.id.isEmpty())
        {
            data.putString(this.id, this.label);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putInt("maxLength", this.maxLength);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("maxLength"))
        {
            this.maxLength = data.getInt("maxLength");
        }
    }
}