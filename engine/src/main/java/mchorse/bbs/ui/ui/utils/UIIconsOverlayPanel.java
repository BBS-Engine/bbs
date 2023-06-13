package mchorse.bbs.ui.ui.utils;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.function.Consumer;

public class UIIconsOverlayPanel extends UIOverlayPanel
{
    public UIIconList icons;

    public UIIconsOverlayPanel(IKey title, Consumer<Icon> callback)
    {
        super(title);

        this.icons = new UIIconList((l) ->
        {
            if (callback != null)
            {
                callback.accept(l.get(0));
            }
        });

        this.icons.relative(this.content).full();

        this.content.add(this.icons);
    }

    public UIIconsOverlayPanel set(String id)
    {
        if (id.isEmpty())
        {
            this.icons.setCurrentScroll(Icons.NONE);

            return this;
        }

        for (Icon icon : this.icons.getList())
        {
            if (icon.id.equals(id))
            {
                this.icons.setCurrentScroll(icon);

                break;
            }
        }

        return this;
    }
}