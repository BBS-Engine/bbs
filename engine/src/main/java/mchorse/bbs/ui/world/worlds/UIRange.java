package mchorse.bbs.ui.world.worlds;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.Range;

public class UIRange extends UIElement
{
    public UIToggle enabled;
    public UITrackpad min;
    public UITrackpad max;

    private Range range;

    public UIRange(IKey enabled)
    {
        this.enabled = new UIToggle(enabled, (b) ->
        {
            this.range.enabled = b.getValue();

            this.rebuild();
        });
        this.min = new UITrackpad((v) -> this.range.min = v.intValue()).integer();
        this.max = new UITrackpad((v) -> this.range.max = v.intValue()).integer();

        this.column().vertical().stretch();

        this.rebuild();
    }

    public void setRange(Range range)
    {
        this.range = range;

        this.enabled.setValue(range.enabled);
        this.min.setValue(range.min);
        this.max.setValue(range.max);

        this.rebuild();
    }

    private void rebuild()
    {
        this.removeAll();
        this.add(this.enabled);

        if (this.enabled.getValue())
        {
            this.add(UI.row(this.min, this.max));
        }

        UIElement container = this.getParentContainer();

        if (container != null)
        {
            container.resize();
        }
    }
}