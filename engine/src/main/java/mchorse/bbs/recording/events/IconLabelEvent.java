package mchorse.bbs.recording.events;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.utils.icons.Icon;

public class IconLabelEvent
{
    public IKey label;
    public Icon icon;
    public int duration = 20;

    public IconLabelEvent(IKey label, Icon icon, int duration)
    {
        this(label, icon);

        this.duration = duration;
    }

    public IconLabelEvent(IKey label, Icon icon)
    {
        this.label = label;
        this.icon = icon;
    }
}