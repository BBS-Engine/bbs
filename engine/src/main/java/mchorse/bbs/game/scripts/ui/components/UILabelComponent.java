package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.l10n.keys.IKey;

/**
 * Label UI component.
 *
 * <p>This component allows you to input one line of text. You can use formatting using "["
 * symbol instead of section symbol.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#label(String)} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create().background();
 *        var label = ui.label("Welcome, [l" + c.getSubject().getName() + "[r!");
 *
 *        label.rxy(0.5, 0.5).wh(100, 20).anchor(0.5);
 *        label.color(0x00ee22).background(0x88000000).labelAnchor(0.5);
 *
 *        bbs.ui.open(ui);
 *    }
 * }</pre>
 */
public class UILabelComponent extends UILabelBaseComponent
{
    public Integer background;
    public float anchorX;
    public float anchorY;

    public UILabelComponent()
    {}

    /**
     * Change background color of this label component by providing hex ARGB.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Add a half transparent black background
     *    uiContext.get("label").background(0x88000000);
     * }</pre>
     */
    public UILabelComponent background(int background)
    {
        this.change("background");

        this.background = background;

        return this;
    }

    /**
     * Change text's anchor point which determines where text will be rendered
     * relative to component's frame both vertically and horizontally.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Position the label's text in the middle of its frame
     *    uiContext.get("label").labelAnchor(0.5);
     * }</pre>
     */
    public UILabelComponent labelAnchor(float anchor)
    {
        return this.labelAnchor(anchor, anchor);
    }

    /**
     * Change text's anchor point which determines where text will be rendered
     * relative to component's frame, with separate vertical and horizontal
     * anchors.
     *
     * <pre>{@code
     *    // Assuming that uiContext is a IScriptUIContext
     *
     *    // Position the label's text in the middle only vertically
     *    uiContext.get("label").labelAnchor(0, 0.5);
     * }</pre>
     */
    public UILabelComponent labelAnchor(float anchorX, float anchorY)
    {
        this.change("anchorX", "anchorY");

        this.anchorX = anchorX;
        this.anchorY = anchorY;

        return this;
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UILabel label = UI.label(IKey.raw(this.getLabel()));

        if (this.background != null)
        {
            label.background(this.background);
        }

        label.labelAnchor(this.anchorX, this.anchorY);

        return this.apply(label, context);
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        UILabel label = (UILabel) element;

        if (key.equals("label"))
        {
            label.label = IKey.raw(this.getLabel());
        }
        else if (key.equals("background"))
        {
            label.background = this.background;
        }
        else if (key.equals("anchorX"))
        {
            label.anchorX = this.anchorX;
        }
        else if (key.equals("anchorY"))
        {
            label.anchorY = this.anchorY;
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (this.background != null)
        {
            data.putInt("background", this.background);
        }

        data.putFloat("anchorX", this.anchorX);
        data.putFloat("anchorY", this.anchorY);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("background"))
        {
            this.background = data.getInt("background");
        }

        if (data.has("anchorX"))
        {
            this.anchorX = data.getFloat("anchorX");
        }

        if (data.has("anchorY"))
        {
            this.anchorY = data.getFloat("anchorY");
        }
    }
}