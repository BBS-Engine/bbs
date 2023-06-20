package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;

/**
 * Clickable button component.
 *
 * <p>This component displays a flat colored box with a label on top, which
 * allows users to trigger the handler script. The value that gets written
 * to UI context's data (if ID is present) is how many times the button
 * was pressed.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#button(String)} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var button = ui.button("Start...").id("button");
 *
 *        button.rxy(0.5, 0.5).wh(160, 20).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *
 *        if (uiContext.getLast() === "button")
 *        {
 *            var data = uiContext.getData();
 *            var pressed = data.getInt("button");
 *
 *            if (pressed >= 100)
 *            {
 *                bbs.send("We have a winner!");
 *            }
 *
 *            uiContext.get("button").label("You pressed: " + pressed);
 *        }
 *    }
 * }</pre>
 */
public class UIButtonComponent extends UILabelBaseComponent
{
    public int background;

    public UIButtonComponent()
    {}

    /**
     * Change button's background color by providing hex RGB.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("button").background(0x00ff00);
     * }</pre>
     */
    public UIButtonComponent background(int background)
    {
        this.change("background");

        this.background = background;

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

        UIButton button = (UIButton) element;

        if (key.equals("label"))
        {
            button.label = IKey.raw(this.getLabel());
        }
        else if (key.equals("background"))
        {
            if (this.background != 0)
            {
                button.color(this.background);
            }
            else
            {
                button.custom = false;
            }
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UIButton button = new UIButton(IKey.raw(this.getLabel()), (b) ->
        {
            if (!this.id.isEmpty())
            {
                this.populateData(context.data);
                context.dirty(this.id, this.updateDelay);
            }
        });

        if (this.background != 0)
        {
            button.color(this.background);
        }

        button.background(this.hasBackground);

        return this.apply(button, context);
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

        if (this.background != 0)
        {
            data.putInt("background", this.background);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("background"))
        {
            this.background = data.getInt("background");
        }
    }
}