package mchorse.bbs.ui.utils.context;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.utils.colors.Colors;

public class ColorfulContextAction extends ContextAction
{
    public int color;

    public ColorfulContextAction(Icon icon, IKey label, Runnable runnable, int color)
    {
        super(icon, label, runnable);

        this.color = color;
    }

    @Override
    protected void renderBackground(UIContext context, int x, int y, int w, int h, boolean hover, boolean selected)
    {
        super.renderBackground(context, x, y, w, h, hover, selected);

        context.draw.box(x, y, x + 2, y + h, Colors.A100 | this.color);
        context.draw.gradientHBox(x + 2, y, x + 24, y + h, Colors.A25 | this.color, this.color);
    }
}
