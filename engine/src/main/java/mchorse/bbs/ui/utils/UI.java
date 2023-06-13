package mchorse.bbs.ui.utils;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.utils.UICameraUtils;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.context.ContextAction;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;

import java.util.function.Consumer;

public class UI
{
    public static UIElement row(UIElement... elements)
    {
        return row(5, elements);
    }

    public static UIElement row(int margin, UIElement... elements)
    {
        return row(margin, 0, elements);
    }

    public static UIElement row(int margin, int padding, UIElement... elements)
    {
        return row(margin, padding, 0, elements);
    }

    public static UIElement row(int margin, int padding, int height, UIElement... elements)
    {
        UIElement element = new UIElement();

        element.row(margin).padding(padding).height(height);
        element.add(elements);

        return element;
    }

    public static UIElement column(UIElement... elements)
    {
        return column(5, elements);
    }

    public static UIElement column(int margin, UIElement... elements)
    {
        return column(margin, 0, elements);
    }

    public static UIElement column(int margin, int padding, UIElement... elements)
    {
        return column(margin, padding, 0, elements);
    }

    public static UIElement column(int margin, int padding, int height, UIElement... elements)
    {
        UIElement element = new UIElement();

        element.column(margin).vertical().stretch().padding(padding).height(height);
        element.add(elements);

        return element;
    }

    public static UILabel label(IKey label)
    {
        return label(label, BBS.getRender().getFont().getHeight());
    }

    public static UILabel label(IKey label, int height)
    {
        return label(label, height, Colors.WHITE);
    }

    public static UILabel label(IKey label, int height, int color)
    {
        UILabel element = new UILabel(label, color);

        element.h(height);

        return element;
    }

    public static UIScrollView scrollView(UIElement... elements)
    {
        return scrollView(5, elements);
    }

    public static UIScrollView scrollView(int margin, UIElement... elements)
    {
        return scrollView(margin, 0, elements);
    }

    public static UIScrollView scrollView(int margin, int padding, UIElement... elements)
    {
        return scrollView(margin, padding, 0, elements);
    }

    public static UIScrollView scrollView(int margin, int padding, int width, UIElement... elements)
    {
        UIScrollView scrollView = new UIScrollView();

        scrollView.column(margin).vertical().stretch().scroll().width(width).padding(padding);

        return scrollView;
    }

    public static void keyframeInterps(UIContext context, KeyframeInterpolation current, Consumer<KeyframeInterpolation> consumer)
    {
        context.replaceContextMenu((menu) ->
        {
            for (KeyframeInterpolation interpolation : KeyframeInterpolation.values())
            {
                ContextAction action;

                if (interpolation == current)
                {
                    action = menu.action(Icons.ADD, UIKeys.C_INTERPOLATION.get(interpolation.key), BBSSettings.primaryColor.get(), () -> consumer.accept(interpolation));
                }
                else
                {
                    action = menu.action(Icons.ADD, UIKeys.C_INTERPOLATION.get(interpolation.key), () -> consumer.accept(interpolation));
                }

                interpolation.setupKeybind(action, UICameraUtils.KEYS_CATEGORY);
            }
        });
    }
}