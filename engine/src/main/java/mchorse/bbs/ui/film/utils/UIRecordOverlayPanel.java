package mchorse.bbs.ui.film.utils;

import mchorse.bbs.film.values.ValueFrames;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class UIRecordOverlayPanel extends UIMessageOverlayPanel
{
    private Consumer<List<String>> callback;

    public UIRecordOverlayPanel(IKey title, IKey message, Consumer<List<String>> callback)
    {
        super(title, message);

        this.callback = callback;

        UIIcon all = new UIIcon(Icons.SPHERE, (b) -> this.submit(null));
        UIIcon position = new UIIcon(Icons.ALL_DIRECTIONS, (b) -> this.submit(Arrays.asList(ValueFrames.GROUP_POSITION)));
        UIIcon rotation = new UIIcon(Icons.REFRESH, (b) -> this.submit(Arrays.asList(ValueFrames.GROUP_ROTATION)));
        UIIcon left = new UIIcon(Icons.LEFT_STICK, (b) -> this.submit(Arrays.asList(ValueFrames.GROUP_LEFT_STICK)));
        UIIcon right = new UIIcon(Icons.RIGHT_STICK, (b) -> this.submit(Arrays.asList(ValueFrames.GROUP_RIGHT_STICK)));
        UIIcon triggers = new UIIcon(Icons.TRIGGER, (b) -> this.submit(Arrays.asList(ValueFrames.GROUP_TRIGGERS)));

        all.tooltip(IKey.lazy("All groups"));
        position.tooltip(IKey.lazy("Only position"));
        rotation.tooltip(IKey.lazy("Only rotation"));
        left.tooltip(IKey.lazy("Left stick"));
        right.tooltip(IKey.lazy("Right stick"));
        triggers.tooltip(IKey.lazy("Triggers"));

        UIElement bar = UI.row(all, position, rotation, left, right, triggers);

        bar.relative(this.content).x(0.5F).y(1F, -6).w(1F, -12).anchor(0.5F, 1F).row().resize();

        this.content.add(bar);
    }

    private void submit(List<String> groups)
    {
        this.close();

        if (this.callback != null)
        {
            this.callback.accept(groups);
        }
    }
}