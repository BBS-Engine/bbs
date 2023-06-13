package mchorse.bbs.settings.ui;

import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueLong;
import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.settings.values.base.IValue;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.UI;

import java.util.List;
import java.util.function.Consumer;

public class UIValueFactory
{
    /* Language key factories */

    public static String getTitleKey(IValue value)
    {
        return value.getId() + ".config.title";
    }

    public static String getCategoryTitleKey(IValue value)
    {
        return getValueLabelKey(value) + ".title";
    }

    public static String getCategoryTooltipKey(IValue value)
    {
        return getValueLabelKey(value) + ".tooltip";
    }

    public static String getValueLabelKey(IValue value)
    {
        List<String> segments = value.getPathSegments();
        String prefix = segments.remove(0);

        return prefix + ".config." + String.join(".", segments);
    }

    public static String getValueCommentKey(IValue value)
    {
        List<String> segments = value.getPathSegments();
        String prefix = segments.remove(0);

        return prefix + ".config." + String.join(".", segments) + "-comment";
    }

    /* UI element factories */

    public static UIToggle booleanUI(ValueBoolean value, Consumer<UIToggle> callback)
    {
        UIToggle booleanToogle = new UIToggle(IKey.lang(getValueLabelKey(value)), value.get(), callback == null ? (toggle) -> value.set(toggle.getValue()) : (toggle) ->
        {
            value.set(toggle.getValue());
            callback.accept(toggle);
        });

        booleanToogle.tooltip(IKey.lang(getValueCommentKey(value)));

        return booleanToogle;
    }

    public static UITrackpad intUI(ValueInt value, Consumer<Double> callback)
    {
        UITrackpad trackpad = new UITrackpad(callback == null ? (v) -> value.set(v.intValue()) : (v) ->
        {
            value.set(v.intValue());
            callback.accept(v);
        });

        trackpad.limit(value.getMin(), value.getMax(), true).delayedInput();
        trackpad.setValue(value.get());
        trackpad.tooltip(IKey.lang(getValueCommentKey(value)));

        return trackpad;
    }

    public static UIColor colorUI(ValueInt value, Consumer<Integer> callback)
    {
        UIColor color = new UIColor(callback == null ? value::set : (integer) ->
        {
            value.set(integer);
            callback.accept(integer);
        });

        color.tooltip(IKey.lang(getValueCommentKey(value)));

        if (value.getSubtype() == ValueInt.Subtype.COLOR_ALPHA)
        {
            color.withAlpha();
        }

        color.setColor(value.get());

        return color;
    }

    public static UITrackpad longUI(ValueLong value, Consumer<Double> callback)
    {
        UITrackpad trackpad = new UITrackpad(callback == null ? (v) -> value.set(v.longValue()) : (v) ->
        {
            value.set(v.longValue());
            callback.accept(v);
        });

        trackpad.limit(value.getMin(), value.getMax(), true).delayedInput();
        trackpad.setValue(value.get());
        trackpad.tooltip(IKey.lang(getValueCommentKey(value)));

        return trackpad;
    }

    public static UITrackpad floatUI(ValueFloat value, Consumer<Double> callback)
    {
        UITrackpad trackpad = new UITrackpad(callback == null ? (v) -> value.set(v.floatValue()) : (v) ->
        {
            value.set(v.floatValue());
            callback.accept(v);
        });

        trackpad.limit(value.getMin(), value.getMax()).delayedInput();
        trackpad.setValue(value.get());
        trackpad.tooltip(IKey.lang(getValueCommentKey(value)));

        return trackpad;
    }

    public static UITrackpad doubleUI(ValueDouble value, Consumer<Double> callback)
    {
        UITrackpad trackpad = new UITrackpad(callback == null ? value::set : (v) ->
        {
            value.set(v);
            callback.accept(v);
        });

        trackpad.limit(value.getMin(), value.getMax()).delayedInput();
        trackpad.setValue(value.get().floatValue());
        trackpad.tooltip(IKey.lang(getValueCommentKey(value)));

        return trackpad;
    }

    public static UITextbox stringUI(ValueString value, Consumer<String> callback)
    {
        UITextbox textbox = new UITextbox(callback == null ? value::set : (string) ->
        {
            value.set(string);
            callback.accept(string);
        });

        textbox.setText(value.get());
        textbox.tooltip(IKey.lang(getValueLabelKey(value)));

        return textbox;
    }

    public static UIElement column(UIElement control, IValue value)
    {
        UIElement element = new UIElement();

        control.removeTooltip();
        element.row(0).preferred(0).height(20);
        element.add(UIValueFactory.label(value), control);

        return commetTooltip(element, value);
    }

    public static UILabel label(IValue value)
    {
        return UI.label(IKey.lang(UIValueFactory.getValueLabelKey(value)), 0).anchor(0, 0.5F);
    }

    public static UIElement commetTooltip(UIElement element, IValue value)
    {
        element.tooltip(IKey.lang(UIValueFactory.getValueCommentKey(value)));

        return element;
    }
}