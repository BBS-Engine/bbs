package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;

import java.util.function.Consumer;

/**
 * Trackpad UI component.
 *
 * <p>This component allows users to input numerical values (integer and double),
 * with optionally a limit range between min and max. Users can also use
 * arrow buttons on the side to increment and decrement the value, and
 * type in value manually.</p>
 *
 * <p>The value that gets written to UI context's data (if ID is present) is
 * the number displayed in the field, if integer option is enabled, then an
 * integer will be written, or double if it's disabled.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#trackpad()} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var trackpad = ui.trackpad(5).id("number").limit(0, 25, true);
 *
 *        trackpad.rxy(0.5, 0.5).wh(160, 20).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *        var data = uiContext.getData();
 *
 *        if (uiContext.getLast() === "number")
 *        {
 *            // If integer wasn't enabled, you would use:
 *            // data.getDouble("number")
 *            if (data.getInt("number") === 19)
 *            {
 *                bbs.send("21");
 *            }
 *        }
 *    }
 * }</pre>
 */
public class UITrackpadComponent extends UIComponent
{
    public double value;
    public double min = -1000000D;
    public double max = 1000000D;
    public boolean integer;

    public double normal = 0.25D;
    public double weak = 0.05D;
    public double strong = 1D;
    public double increment = 1D;

    /**
     * Set the value that of trackpad component.
     */
    public UITrackpadComponent value(double value)
    {
        this.change("value");

        this.value = value;

        return this;
    }

    /**
     * Set the minimum that this trackpad component can let the user pick.
     */
    public UITrackpadComponent min(double min)
    {
        this.change("min");

        this.min = min;

        return this;
    }

    /**
     * Set the maximum that this trackpad component can let the user pick.
     */
    public UITrackpadComponent max(double max)
    {
        this.change("max");

        this.max = max;

        return this;
    }

    /**
     * Set this trackpad component to accept only whole numbers.
     */
    public UITrackpadComponent integer()
    {
        return this.integer(true);
    }

    /**
     * Toggle integer option, when passed <code>true</code> then this trackpad
     * component will accept only whole numbers, and when passed <code>false</code>,
     * then both whole and floating point numbers can be accepted by this trackpad.
     */
    public UITrackpadComponent integer(boolean integer)
    {
        this.change("integer");

        this.integer = integer;

        return this;
    }

    /**
     * Convenience method that allows to set minimum and maximum, i.e. value range,
     * that this trackpad field can accept.
     */
    public UITrackpadComponent limit(double min, double max)
    {
        return this.min(min).max(max);
    }

    /**
     * Convenience method that allows to set minimum, maximum, and integer options
     * that this trackpad field can accept.
     */
    public UITrackpadComponent limit(double min, double max, boolean integer)
    {
        return this.min(min).max(max).integer(integer);
    }

    /**
     * Changes the amplitudes of this trackpad fields, i.e. how much value changes when
     * moving the cursor horizontally. Weak (<code>Alt</code> amplitude gets set 5 times
     * weaker than input value, and strong (<code>Shift</code>) amplitude gets set 5 times
     * stronger than input value.
     */
    public UITrackpadComponent amplitudes(double normal)
    {
        return this.amplitudes(normal, normal / 5, normal * 5);
    }

    /**
     * Changes the amplitudes of this trackpad fields, i.e. how much value changes when
     * moving the cursor horizontally.
     *
     * @param normal Value change per pixel when no modifiers is held.
     * @param weak Value change per pixel when alt is held.
     * @param strong Value change per pixel when shift is held.
     */
    public UITrackpadComponent amplitudes(double normal, double weak, double strong)
    {
        this.change("normal", "weak", "strong");

        this.normal = normal;
        this.weak = weak;
        this.strong = strong;

        return this;
    }

    /**
     * Changes the incremental value of this trackpad fields, i.e. how much being added
     * or subtracted when user presses &lt; and &gt; buttons on the sides of the
     * trackpad value.
     *
     * @param increment Value change per click on increment buttons.
     */
    public UITrackpadComponent increment(double increment)
    {
        this.change("increment");

        this.increment = increment;

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

        UITrackpad trackpad = (UITrackpad) element;

        if (key.equals("value"))
        {
            trackpad.setValue(this.value);
        }
        else if (key.equals("min"))
        {
            trackpad.min = this.min;
        }
        else if (key.equals("max"))
        {
            trackpad.max = this.max;
        }
        else if (key.equals("integer"))
        {
            trackpad.integer = this.integer;
        }
        else if (key.equals("normal"))
        {
            trackpad.normal = this.normal;
        }
        else if (key.equals("weak"))
        {
            trackpad.weak = this.weak;
        }
        else if (key.equals("strong"))
        {
            trackpad.strong = this.strong;
        }
        else if (key.equals("increment"))
        {
            trackpad.increment = this.increment;
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UITrackpad element = new UITrackpad();

        element.callback = (v) ->
        {
            if (!this.id.isEmpty())
            {
                if (element.integer)
                {
                    context.data.putInt(this.id, v.intValue());
                }
                else
                {
                    context.data.putDouble(this.id, v);
                }

                context.dirty(this.id, this.updateDelay);
            }
        };

        element.setValue(this.value);
        element.min = this.min;
        element.max = this.max;

        element.integer = this.integer;

        element.normal = this.normal;
        element.weak = this.weak;
        element.strong = this.strong;
        element.increment = this.increment;

        return this.apply(element, context);
    }

    @Override
    @DiscardMethod
    public void populateData(MapType data)
    {
        super.populateData(data);

        if (!this.id.isEmpty())
        {
            if (this.integer)
            {
                data.putInt(this.id, (int) this.value);
            }
            else
            {
                data.putDouble(this.id, this.value);
            }
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putDouble("value", this.value);
        data.putDouble("min", this.min);
        data.putDouble("max", this.max);

        data.putBool("integer", this.integer);

        data.putDouble("normal", this.normal);
        data.putDouble("weak", this.weak);
        data.putDouble("strong", this.strong);
        data.putDouble("increment", this.increment);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("value")) this.value = data.getDouble("value");
        if (data.has("min")) this.min = data.getDouble("min");
        if (data.has("max")) this.max = data.getDouble("max");
        if (data.has("integer")) this.integer = data.getBool("integer");
        if (data.has("normal")) this.normal = data.getDouble("normal");
        if (data.has("weak")) this.weak = data.getDouble("weak");
        if (data.has("strong")) this.strong = data.getDouble("strong");
        if (data.has("increment")) this.increment = data.getDouble("increment");
    }
}