package mchorse.bbs.ui.dashboard.panels.overlay;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.list.UIDataPathList;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.overlay.UIConfirmOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.DataPath;

import java.util.function.Consumer;

public abstract class UICRUDOverlayPanel extends UIOverlayPanel
{
    public UIIcon add;
    public UIIcon dupe;
    public UIIcon rename;
    public UIIcon remove;
    public UISearchList<DataPath> names;
    public UIDataPathList namesList;

    protected Consumer<String> callback;

    public UICRUDOverlayPanel(IKey title, Consumer<String> callback)
    {
        super(title);

        this.callback = callback;

        this.add = new UIIcon(Icons.ADD, this::addNewData);
        this.add.context((menu) -> menu.action(Icons.FOLDER, UIKeys.PANELS_MODALS_ADD_FOLDER_TITLE, this::addNewFolder));
        this.dupe = new UIIcon(Icons.DUPE, this::dupeData);
        this.rename = new UIIcon(Icons.EDIT, this::renameData);
        this.remove = new UIIcon(Icons.REMOVE, this::removeData);

        this.names = new UISearchList<>(new UIDataPathList((list) ->
        {
            if (this.callback != null)
            {
                this.callback.accept(list.get(0).toString());
            }
        }));
        this.names.relative(this.content).full().x(6).w(1F, -12);
        this.namesList = (UIDataPathList) this.names.list;
        this.names.label(UIKeys.SEARCH);
        this.content.add(this.names);

        this.icons.add(this.add, this.dupe, this.rename, this.remove);
    }

    /* CRUD */

    protected void addNewData(UIIcon element)
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.ADD,
            UIKeys.PANELS_MODALS_ADD,
            (str) -> this.addNewData(this.namesList.getPath(str).toString())
        );

        panel.text.filename();

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    protected abstract void addNewData(String name);

    protected void addNewFolder()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.PANELS_MODALS_ADD_FOLDER_TITLE,
            UIKeys.PANELS_MODALS_ADD_FOLDER,
            (str) -> this.addNewFolder(this.namesList.getPath(str).toString())
        );

        panel.text.filename();

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    protected abstract void addNewFolder(String path);

    protected void dupeData(UIIcon element)
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.DUPE,
            UIKeys.PANELS_MODALS_DUPE,
            (str) -> this.dupeData(this.namesList.getPath(str).toString())
        );

        panel.text.setText(this.namesList.getCurrentFirst().getLast());
        panel.text.filename();

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    protected abstract void dupeData(String name);

    protected void renameData(UIIcon element)
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.RENAME,
            UIKeys.PANELS_MODALS_RENAME,
            (str) -> this.renameData(this.namesList.getPath(str).toString())
        );

        if (this.namesList.isFolderSelected())
        {
            if (this.namesList.getCurrentFirst().equals("../"))
            {
                return;
            }

            panel = new UIPromptOverlayPanel(
                UIKeys.PANELS_MODALS_RENAME_FOLDER_TITLE,
                UIKeys.PANELS_MODALS_RENAME_FOLDER,
                (str) -> this.renameFolder(this.namesList.getPath(str).toString())
            );
        }

        panel.text.setText(this.namesList.getCurrentFirst().getLast());
        panel.text.filename();

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    protected abstract void renameData(String name);

    protected abstract void renameFolder(String name);

    protected void removeData(UIIcon element)
    {
        UIConfirmOverlayPanel panel = new UIConfirmOverlayPanel(
            UIKeys.REMOVE,
            UIKeys.PANELS_MODALS_REMOVE,
            (confirm) ->
            {
                if (confirm) this.removeData();
            }
        );

        if (this.namesList.isFolderSelected())
        {
            if (this.namesList.getCurrentFirst().equals("../"))
            {
                return;
            }

            panel = new UIConfirmOverlayPanel(
                UIKeys.PANELS_MODALS_REMOVE_FOLDER_TITLE,
                UIKeys.PANELS_MODALS_REMOVE_FOLDER,
                (confirm) ->
                {
                    if (confirm) this.removeFolder();
                }
            );
        }

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    protected abstract void removeData();

    protected abstract void removeFolder();
}