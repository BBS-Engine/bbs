package mchorse.bbs.ui.game.utils;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UITextboxOverlayPanel;
import mchorse.bbs.ui.game.utils.overlays.UIContentNamesOverlayPanel;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.renderers.InputRenderer;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.Interpolations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class UIDataUtils
{
    public static UITextbox fullWindowContext(UITextbox text, IKey title)
    {
        text.context((menu) -> menu.action(Icons.EDIT, UIKeys.OVERLAYS_TEXT_FULLSCREEN, () ->
        {
            UITextboxOverlayPanel panel = new UITextboxOverlayPanel(title, text);
            UIOverlay overlay = new UIOverlay();

            panel.w(1F, -30).h(54);
            UIOverlay.addOverlay(text.getContext(), overlay, panel);
        }));

        return text;
    }

    public static void requestNames(ContentType type, Consumer<List<String>> consumer)
    {
        consumer.accept(new ArrayList<String>(type.getManager().getKeys()));
    }

    public static void openPicker(UIContext context, ContentType type, String value, Consumer<String> callback)
    {
        requestNames(type, (names) ->
        {
            clearEmptyFolders(names);

            UIContentNamesOverlayPanel overlay = new UIContentNamesOverlayPanel(type.getPickLabel(), type, names, callback);

            overlay.set(value);
            UIOverlay.addOverlay(context, overlay, 0.5F, 0.7F);
        });
    }

    private static void clearEmptyFolders(List<String> names)
    {
        Iterator<String> it = names.iterator();

        while (it.hasNext())
        {
            if (it.next().endsWith("/"))
            {
                it.remove();
            }
        }
    }

    public static void renderRightClickHere(UIContext context, Area area)
    {
        int primary = BBSSettings.primaryColor.get();
        double ticks = context.getTickTransition() % 80D;
        double factor = Math.abs(ticks / 80D * 2 - 1F);

        factor = Interpolation.EXP_INOUT.interpolate(0, 1, factor);

        double factor2 = Interpolations.envelope(ticks, 37, 40, 40, 43);

        factor2 = Interpolation.CUBIC_OUT.interpolate(0, 1, factor2);

        int offset = (int) (factor * 70 + factor2 * 2);

        context.draw.dropCircleShadow(area.mx(), area.my() + (int) (factor * 70), 16, 0, 16, Colors.A50 | primary, primary);
        InputRenderer.renderMouseButtons(context.draw, area.mx() - 6, area.my() - 8 + offset, 0, false, factor2 > 0, false, false);

        String label = UIKeys.RIGHT_CLICK.get();
        int w = (int) (area.w / 1.1F);
        int color = Colors.mulRGB(0x444444, 1 - (float) factor);

        context.draw.wallText(context.font, label, area.mx() - w / 2, area.my() - 20, color, w, 12, 0.5F, 1);

        context.draw.gradientVBox(area.x, area.my() + 20, area.ex(), area.my() + 40, 0, Colors.A100);
        context.draw.box(area.x, area.my() + 40, area.ex(), area.my() + 90, Colors.A100);
    }

    public static UICirculate createTargetCirculate(TargetMode defaultTarget, Consumer<TargetMode> callback)
    {
        UICirculate button = new UICirculate((b) ->
        {
            if (callback != null)
            {
                callback.accept(TargetMode.values()[b.getValue()]);
            }
        });

        for (TargetMode target : TargetMode.values())
        {
            button.addLabel(UIKeys.C_TARGET.get(target));
        }

        button.setValue(defaultTarget.ordinal());

        return button;
    }
}