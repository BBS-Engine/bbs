package mchorse.bbs.ui.utils.context;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.context.UISimpleContextMenu;
import mchorse.bbs.ui.framework.elements.events.UIRemovedEvent;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.ui.utils.keys.Keybind;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ContextMenuManager
{
    public List<ContextAction> actions = new ArrayList<>();
    public Consumer<UIRemovedEvent> onClose;

    public ContextMenuManager onClose(Consumer<UIRemovedEvent> onClose)
    {
        this.onClose = onClose;

        return this;
    }

    public ContextAction action(IKey label, Runnable runnable)
    {
        return this.action(Icons.NONE, label, runnable);
    }

    public ContextAction action(Icon icon, IKey label, Runnable runnable)
    {
        if (icon == null || label == null)
        {
            throw new IllegalStateException("Icon (" + icon + ") and/or label (" + label + ") is null!");
        }

        return this.action(new ContextAction(icon, label, runnable));
    }

    public ContextAction action(Icon icon, IKey label, int color, Runnable runnable)
    {
        if (icon == null || label == null)
        {
            throw new IllegalStateException("Icon (" + icon + ") and/or label (" + label + ") is null!");
        }

        return this.action(new ColorfulContextAction(icon, label, runnable, color));
    }

    public ContextAction action(ContextAction action)
    {
        this.actions.add(action);

        return action;
    }

    public UISimpleContextMenu create()
    {
        UISimpleContextMenu contextMenu = new UISimpleContextMenu();

        contextMenu.actions.add(this.actions);
        contextMenu.getEvents().register(UIRemovedEvent.class, this.onClose);

        for (int i = 0; i < this.actions.size(); i++)
        {
            ContextAction action = this.actions.get(i);

            if (action.keys != null)
            {
                Keybind register = contextMenu.keys().register(new KeyCombo(action.label, action.keys), () ->
                {
                    if (action.runnable != null)
                    {
                        action.runnable.run();
                    }

                    contextMenu.removeFromParent();
                });

                if (action.keyCategory != null)
                {
                    register.category(action.keyCategory);
                }
            }
        }

        return contextMenu.isEmpty() ? null : contextMenu;
    }
}