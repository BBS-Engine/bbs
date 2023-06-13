package mchorse.bbs.ui.framework.tooltips;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.l10n.keys.IKey;

public interface ITooltip
{
    public IKey getLabel();

    public void renderTooltip(UIContext context);
}
