package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.Map;
import java.util.function.Consumer;

public abstract class UIMapOverlayPanel <T extends IMapSerializable> extends UIStringOverlayPanel
{
    protected IKey addContext = IKey.EMPTY;
    protected IKey copyContext = IKey.EMPTY;
    protected IKey pasteContext = IKey.EMPTY;
    protected IKey renameContext = IKey.EMPTY;
    protected IKey removeContext = IKey.EMPTY;

    protected IKey addOverlay = IKey.EMPTY;
    protected IKey pasteOverlay = IKey.EMPTY;
    protected IKey renameOverlay = IKey.EMPTY;
    protected IKey removeOverlay = IKey.EMPTY;

    private Map<String, T> map;

    public UIMapOverlayPanel(IKey title, Map<String, T> map, Consumer<String> callback)
    {
        super(title, false, map.keySet(), callback);

        this.map = map;

        this.strings.context((menu) ->
        {
            menu.shadow().action(Icons.ADD, this.addContext, this::addState);

            if (!this.strings.list.isDeselected())
            {
                menu.action(Icons.COPY, this.copyContext, this::copyState);

                try
                {
                    MapType m = Window.getClipboardMap(this.getCopyKey());

                    if (m != null)
                    {
                        T data = this.create();

                        data.fromData(m);
                        menu.action(Icons.PASTE, this.pasteContext, () -> this.pasteState(data));
                    }
                }
                catch (Exception e)
                {}

                menu.action(Icons.EDIT, this.renameContext, this::renameState);
                menu.action(Icons.REMOVE, this.removeContext, Colors.NEGATIVE, this::removeState);
            }
        });
    }

    protected String getCopyKey()
    {
        return "_MapCopy";
    }

    protected abstract T create();

    /* Context menu modals */

    private void addState()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.ADD,
            this.addOverlay,
            (name) -> this.addState(name, null)
        );

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    protected void addState(String name, T data)
    {
        if (!this.map.containsKey(name))
        {
            if (data == null)
            {
                data = this.create();
            }

            this.map.put(name, data);
            this.strings.list.add(name);
            this.strings.list.sort();

            this.set(name);
            this.accept(name);
        }
    }

    private void copyState()
    {
        String key = this.strings.list.getCurrentFirst();
        MapType data = this.map.get(key).toData();

        Window.setClipboard(data, this.getCopyKey());
    }

    protected void pasteState(T state)
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.PASTE,
            this.pasteOverlay,
            (name) -> this.addState(name, state)
        );

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void renameState()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.RENAME,
            this.renameOverlay,
            this::renameState
        );

        panel.text.setText(this.strings.list.getCurrentFirst());

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    protected void renameState(String name)
    {
        String current = this.strings.list.getCurrentFirst();

        if (!this.map.containsKey(name))
        {
            T state = this.map.remove(current);

            this.map.put(name, state);
            this.strings.list.remove(current);
            this.strings.list.add(name);
            this.strings.list.sort();

            this.set(name);
            this.accept(name);
        }
    }

    private void removeState()
    {
        UIConfirmOverlayPanel panel = new UIConfirmOverlayPanel(
            UIKeys.CONFIRM,
            this.removeOverlay,
            this::removeState
        );

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    protected void removeState(boolean confirm)
    {
        if (confirm)
        {
            int index = this.strings.list.getIndex();
            String key = this.strings.list.getCurrentFirst();

            this.strings.list.remove(key);
            this.map.remove(key);
            this.strings.list.setIndex(Math.max(index - 1, 0));

            String name = this.getValue();

            this.set(name);
            this.accept(name);
        }
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        super.renderBackground(context);

        if (this.map.size() <= 1)
        {
            UIDataUtils.renderRightClickHere(context, this.content.area);
        }
    }
}