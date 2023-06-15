package mchorse.bbs.ui.utils.context;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.utils.colors.Colors;

public class ContextAction
{
    public Icon icon;
    public IKey label;
    public Runnable runnable;

    public IKey keyCategory;
    public int[] keys;

    public ContextAction(Icon icon, IKey label, Runnable runnable)
    {
        this.icon = icon;
        this.label = label;
        this.runnable = runnable;
    }

    public ContextAction key(IKey keyCategory, int... keys)
    {
        this.keyCategory = keyCategory;

        return this.key(keys);
    }

    public ContextAction key(int... keys)
    {
        this.keys = keys;

        return this;
    }

    public int getWidth(FontRenderer font)
    {
        return 28 + font.getWidth(this.label.get());
    }

    public void render(UIContext context, FontRenderer font, int x, int y, int w, int h, boolean hover, boolean selected)
    {
        this.renderBackground(context, x, y, w, h, hover, selected);

        context.batcher.icon(this.icon, x + 2, y + h / 2, 0, 0.5F);
        context.batcher.text(font, this.label.get(), x + 22, y + (h - font.getHeight()) / 2 + 1, Colors.WHITE, false);
    }

    protected void renderBackground(UIContext context, int x, int y, int w, int h, boolean hover, boolean selected)
    {
        if (hover)
        {
            context.batcher.box(x, y, x + w, y + h, Colors.A50 | BBSSettings.primaryColor.get());
        }
    }
}
