package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextarea;
import mchorse.bbs.ui.framework.elements.input.text.utils.TextLine;

/**
 * Text area UI component.
 *
 * <p>This component allows users to input multiple lines of text (for single-line
 * input use {@link UITextboxComponent} component). This could be used to let players
 * input a multi-line description about themselves.</p>
 *
 * <p>The value that gets written to UI context's data (if ID is present) is
 * the multi-line string that user typed into the component.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#textarea(String)} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var s = c.getSubject();
 *        var story = s.getStates().getString("story");
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var textarea = ui.textarea(story).id("textarea").tooltip("Tell us your story...");
 *
 *        // Don't send updates too often, only half a second after the player
 *        // stopped typing into a text area
 *        textarea.updateDelay(500);
 *        textarea.rxy(0.5, 0.5).wh(300, 200).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *        var data = uiContext.getData();
 *
 *        if (uiContext.getLast() === "textarea")
 *        {
 *            // Write down the input
 *            c.getSubject().getStates().setString("story", data.getString("textarea"));
 *        }
 *    }
 * }</pre>
 */
public class UITextareaComponent extends UILabelBaseComponent
{
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
            ((UITextarea) element).setText(this.label);
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UITextarea<TextLine> element = new UITextarea<TextLine>((t) ->
        {
            if (!this.id.isEmpty())
            {
                context.data.putString(this.id, t);
                context.dirty(this.id, this.updateDelay);
            }
        });

        element.wrap().setText(this.label);
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
}