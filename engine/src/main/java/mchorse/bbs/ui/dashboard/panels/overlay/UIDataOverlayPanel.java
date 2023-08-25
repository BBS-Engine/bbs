package mchorse.bbs.ui.dashboard.panels.overlay;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.manager.FolderManager;
import mchorse.bbs.utils.manager.data.AbstractData;

import java.io.File;
import java.util.function.Consumer;

public class UIDataOverlayPanel <T extends AbstractData> extends UICRUDOverlayPanel
{
    protected UIDataDashboardPanel<T> panel;
    protected T transientCopy;

    public UIDataOverlayPanel(IKey title, UIDataDashboardPanel<T> panel, Consumer<String> callback)
    {
        super(title, callback);

        this.panel = panel;

        this.namesList.context((menu) ->
        {
            menu.action(Icons.FOLDER, UIKeys.PANELS_MODALS_ADD_FOLDER_TITLE, this::addNewFolder);

            if (this.panel.getData() != null)
            {
                menu.action(Icons.COPY, UIKeys.PANELS_CONTEXT_COPY, this::copy);
            }

            try
            {
                MapType data = Window.getClipboardMap("_ContentType_" + this.panel.getType().getId());

                if (data != null)
                {
                    menu.action(Icons.PASTE, UIKeys.PANELS_CONTEXT_PASTE, () -> this.paste(data));
                }
            }
            catch (Exception e)
            {}

            menu.action(Icons.FOLDER, UIKeys.PANELS_CONTEXT_OPEN, () ->
            {
                File folder = ((FolderManager) this.panel.getType().getManager()).getFolder();

                UIUtils.openFolder(new File(folder, this.namesList.getPath().toString()));
            });
        });
    }

    private void copy()
    {
        Window.setClipboard(this.panel.getData().toData(), "_ContentType_" + this.panel.getType().getId());
    }

    private void paste(MapType data)
    {
        this.transientCopy = (T) this.panel.getType().getManager().create("", data);

        this.addNewData(this.add);
    }

    /* CRUD */

    @Override
    protected void addNewData(String name)
    {
        if (!this.namesList.hasInHierarchy(name))
        {
            this.panel.save();

            this.namesList.addFile(name);

            if (this.transientCopy == null)
            {
                this.transientCopy = (T) this.panel.getType().getManager().create(name);

                this.fillDefaultData(this.transientCopy);
            }
            else
            {
                this.transientCopy.setId(name);
            }

            this.panel.fill(this.transientCopy);
        }

        this.transientCopy = null;
    }

    @Override
    protected void addNewFolder(String path)
    {
        if (((FolderManager) this.panel.getType().getManager()).addFolder(path))
        {
            this.panel.requestNames();
        }
    }

    protected void fillDefaultData(T data)
    {
        this.panel.fillDefaultData(data);
    }

    @Override
    protected void dupeData(String name)
    {
        if (this.panel.getData() != null && !this.namesList.getList().contains(name))
        {
            this.panel.save();
            this.panel.getType().getManager().save(name, this.panel.getData().toData());
            this.namesList.addFile(name);

            T data = (T) this.panel.getType().getManager().create(name, this.panel.getData().toData());

            this.panel.fill(data);
        }
    }

    @Override
    protected void renameData(String name)
    {
        if (this.panel.getData() != null && !this.namesList.getList().contains(name))
        {
            this.panel.getType().getManager().rename(this.panel.getData().getId(), name);

            this.namesList.removeFile(this.panel.getData().getId());
            this.namesList.addFile(name);

            this.panel.getData().setId(name);
        }
    }

    @Override
    protected void renameFolder(String name)
    {
        String path = this.namesList.getCurrentFirst().toString();

        if (((FolderManager) this.panel.getType().getManager()).renameFolder(path, name))
        {
            if (this.panel.getData() != null)
            {
                String id = this.panel.getData().getId();

                this.panel.getData().setId(name + "/" + id.substring(path.length()));
            }

            this.panel.requestNames();
        }
    }

    @Override
    protected void removeData()
    {
        if (this.panel.getData() != null)
        {
            this.panel.getType().getManager().delete(this.panel.getData().getId());

            this.namesList.removeFile(this.panel.getData().getId());
            this.panel.fill(null);
        }
    }

    @Override
    protected void removeFolder()
    {
        String path = this.namesList.getCurrentFirst().toString();

        if (((FolderManager) this.panel.getType().getManager()).deleteFolder(path))
        {
            this.panel.requestNames();
        }
    }
}