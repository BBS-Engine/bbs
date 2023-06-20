package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;

/**
 * Toggle UI component.
 *
 * <p>This component allows users to input boolean value (yes/no, true/false,
 * <code>1</code>/<code>0</code>). The value that gets written to UI context's data
 * (if ID is present) is boolean.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#toggle(String)} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var trackpad = ui.toggle("Show", false).id("toggle");
 *        var label = ui.label("You can see this label").id("label").visible(false);
 *
 *        trackpad.rxy(0.5, 0.5).wh(160, 20).anchor(0.5);
 *        label.rx(0.5).ry(0.5, 25).wh(160, 20).anchor(0.5).labelAnchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *        var data = uiContext.getData();
 *
 *        if (uiContext.getLast() === "toggle")
 *        {
 *            uiContext.get("label").visible(uiContext.getData().getBool("toggle"));
 *        }
 *    }
 * }</pre>
 */
public class UIToggleComponent extends UILabelBaseComponent
{
    public boolean state;

    /**
     * Change component's toggled state.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *    uiContext.get("toggle").state(true);
     * }</pre>
     */
    public UIToggleComponent state(boolean state)
    {
        this.change("state");

        this.state = state;

        return this;
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        UIToggle toggle = (UIToggle) element;

        if (key.equals("label"))
        {
            toggle.label = IKey.raw(this.getLabel());
        }
        else if (key.equals("state"))
        {
            toggle.setValue(this.state);
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UIToggle toggle = new UIToggle(IKey.raw(this.getLabel()), (b) ->
        {
            if (!this.id.isEmpty())
            {
                context.data.putBool(this.id, b.getValue());
                context.dirty(this.id, this.updateDelay);
            }
        });

        toggle.setValue(this.state);

        return this.apply(toggle, context);
    }

    @Override
    @DiscardMethod
    public void populateData(MapType data)
    {
        super.populateData(data);

        if (!this.id.isEmpty())
        {
            data.putBool(this.id, this.state);
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putBool("state", this.state);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("state"))
        {
            this.state = data.getBool("state");
        }
    }
}