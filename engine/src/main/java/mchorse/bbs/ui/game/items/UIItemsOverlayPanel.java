package mchorse.bbs.ui.game.items;

import mchorse.bbs.game.items.Item;
import mchorse.bbs.game.items.ItemEntry;
import mchorse.bbs.game.items.ItemManager;
import mchorse.bbs.game.items.ItemRender;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.overlay.UIConfirmOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIItemsOverlayPanel extends UIOverlayPanel
{
    public UISearchList<ItemEntry> itemList;

    public UIIcon add;
    public UIIcon rename;
    public UIIcon remove;

    private UIItemsPanel panel;

    public UIItemsOverlayPanel(IKey title, UIItemsPanel panel)
    {
        super(title);

        this.panel = panel;

        this.itemList = new UISearchList<ItemEntry>(new UIItemList((l) -> this.panel.fill(l.get(0), false)));
        this.itemList.search.placeholder(UIKeys.SEARCH);
        this.itemList.context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.ITEM_PANEL_CONTEXT_ADD, this::addItem);

            if (this.itemList.list.isSelected())
            {
                menu.action(Icons.EDIT, UIKeys.ITEM_PANEL_CONTEXT_RENAME, this::renameItem);
                menu.action(Icons.REMOVE, UIKeys.ITEM_PANEL_CONTEXT_REMOVE, this::removeItem);
            }
        });
        this.itemList.relative(this.content).full();

        this.add = new UIIcon(Icons.ADD, (b) -> this.addItem());
        this.rename = new UIIcon(Icons.EDIT, (b) -> this.renameItem());
        this.remove = new UIIcon(Icons.REMOVE, (b) -> this.removeItem());

        this.content.add(this.itemList);
        this.icons.add(this.add, this.rename, this.remove);
    }

    private void addItem()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.ADD,
            UIKeys.ITEM_PANEL_ADD_DESCRIPTION,
            this::addItem
        );

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void addItem(String name)
    {
        Link id = Link.create(name);

        if (this.panel.getItems().items.containsKey(id))
        {
            UIMessageOverlayPanel panel = new UIMessageOverlayPanel(UIKeys.ERROR, UIKeys.ITEM_PANEL_ALREADY_EXIST);

            UIOverlay.addOverlay(this.getContext(), panel);

            return;
        }

        ItemRender render = new ItemRender();
        Item item = new Item();
        ItemEntry entry = new ItemEntry(item, render);

        item.setId(id);

        this.panel.getItems().register(entry);
        this.panel.requestNames();
        this.panel.fill(entry, true);
        this.panel.dirty();
    }

    private void renameItem()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.ADD,
            UIKeys.ITEM_PANEL_RENAME_DESCRIPTION,
            this::renameItem
        );

        panel.text.setText(this.panel.getEntry().item.getId().toString());

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void renameItem(String name)
    {
        Link id = Link.create(name);
        ItemManager items = this.panel.getItems();

        if (items.items.containsKey(id))
        {
            UIMessageOverlayPanel panel = new UIMessageOverlayPanel(UIKeys.ERROR, UIKeys.ITEM_PANEL_ALREADY_EXIST);

            UIOverlay.addOverlay(this.getContext(), panel);

            return;
        }

        items.items.remove(this.panel.getEntry().item.getId());
        this.panel.getEntry().item.setId(id);
        items.register(this.panel.getEntry());

        this.panel.requestNames();
        this.panel.fill(this.panel.getEntry(), true);
        this.panel.dirty();
    }

    private void removeItem()
    {
        UIConfirmOverlayPanel panel = new UIConfirmOverlayPanel(
            UIKeys.REMOVE,
            UIKeys.ITEM_PANEL_REMOVE_DESCRIPTION,
            this::removeItem
        );

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void removeItem(Boolean confirm)
    {
        if (!confirm)
        {
            return;
        }

        ItemManager items = this.panel.getItems();

        items.items.remove(this.panel.getEntry().item.getId());

        int index = this.itemList.list.getIndex();
        ItemEntry entry = items.items.isEmpty() ? null : this.itemList.list.getList().get(index == 0 ? 1 : index - 1);

        this.panel.requestNames();
        this.panel.fill(entry, true);
        this.panel.dirty();
    }
}