package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.ui.utils.keys.KeybindManager;

public abstract class Tool
{
    public static final IKey CATEGORY_KEY = UIKeys.APERTURE_KEYS_CATEGORY;

    public Icon contextIcon;
    public KeyCombo combo;

    public Tool(Icon contextIcon, KeyCombo combo)
    {
        this.contextIcon = contextIcon;
        this.combo = combo;
    }

    public boolean canApply(ToolContext context)
    {
        return true;
    }

    public abstract boolean apply(ToolContext context);

    public void addContext(ToolContext context, ContextMenuManager menu)
    {
        menu.action(this.contextIcon, this.combo.label, () -> this.apply(context));
    }

    public void addKey(ToolContext context, KeybindManager keys)
    {
        context.timeline.keys().register(this.combo, () -> this.apply(context)).category(CATEGORY_KEY);
    }
}