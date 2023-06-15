package mchorse.bbs.ui.dashboard.list;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.DataPath;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class UIDataPathList extends UIList<DataPath>
{
    /**
     * A list of paths.
     */
    private Set<DataPath> hierarchy = new HashSet<DataPath>();

    /**
     * Path in which current list is located. It's expected to be
     * something like "abc/def/ghi" (i.e. without trailing slash).
     */
    private DataPath path = new DataPath(true);

    /**
     * Icon that is used to render "files."
     */
    private Icon fileIcon = Icons.FILE;

    private DataPath previousPath;

    public UIDataPathList(Consumer<List<DataPath>> callback)
    {
        super(null);

        this.scroll.scrollItemSize = 16;
        this.callback = (l) -> this.fileCallback(callback, l);
    }

    private void fileCallback(Consumer<List<DataPath>> callback, List<DataPath> strings)
    {
        DataPath dataPath = strings.get(0);

        if (dataPath.folder)
        {
            if (Objects.equals(this.previousPath, dataPath))
            {
                DataPath newPath;

                if (dataPath.getLast().equals(".."))
                {
                    newPath = this.path.getParent();
                }
                else
                {
                    newPath = dataPath;
                }

                this.goTo(newPath);
            }
        }
        else
        {
            callback.accept(strings);
        }

        this.previousPath = dataPath.copy();
    }

    public void setFileIcon(Icon icon)
    {
        this.fileIcon = icon;
    }

    public DataPath getPath()
    {
        return this.path;
    }

    public DataPath getPath(String name)
    {
        if (this.path.strings.isEmpty())
        {
            return new DataPath(name);
        }

        DataPath copy = this.path.copy();

        copy.combine(new DataPath(name));

        return copy;
    }

    public boolean isFolderSelected()
    {
        DataPath item = this.getCurrentFirst();

        return item != null && item.folder;
    }

    public void fill(Collection<String> hierarchy)
    {
        this.hierarchy.clear();

        for (String string : hierarchy)
        {
            this.hierarchy.add(new DataPath(string));
        }

        this.goTo(DataPath.EMPTY);
    }

    private void goTo(DataPath path)
    {
        this.path.copy(path);
        this.previousPath = null;

        this.filter("");
        this.deselect();
        this.updateStrings();
    }

    private void updateStrings()
    {
        Set<DataPath> paths = new HashSet<DataPath>();

        if (!this.path.strings.isEmpty())
        {
            DataPath copy = this.path.copy();

            copy.strings.add("..");
            paths.add(copy);
        }

        for (DataPath dataPath : this.hierarchy)
        {
            if (dataPath.startsWith(this.path, 1))
            {
                paths.add(dataPath);
            }
            else if (dataPath.startsWith(this.path) && !dataPath.equals(this.path))
            {
                DataPath to = dataPath.getTo(this.path.strings.size() + 1);

                paths.add(to);
            }
        }

        this.list.clear();
        this.list.addAll(paths);

        this.sort();
        this.update();
    }

    public boolean hasInHierarchy(String path)
    {
        return this.hasInHierarchy(new DataPath(path));
    }

    public boolean hasInHierarchy(DataPath path)
    {
        return this.hierarchy.contains(path);
    }

    /**
     * Add file path to this hierarchy.
     */
    public void addFile(String path)
    {
        DataPath dataPath = this.getFilename(path);

        if (dataPath != null)
        {
            this.hierarchy.add(dataPath);

            this.add(dataPath);
            this.sort();
            this.setCurrentFile(path);
        }
    }

    /**
     * Removes given path from the hierarchy and currently displayed list.
     */
    public void removeFile(String path)
    {
        DataPath dataPath = this.getFilename(path);

        if (dataPath != null && this.hasInHierarchy(path))
        {
            this.hierarchy.remove(dataPath);

            this.remove(dataPath);
            this.deselect();
        }
    }

    /**
     * Get the filename of the path. It returns filename only if
     * given path matches the current path in the hierarchy, otherwise
     * it will return {@code null}.
     */
    private DataPath getFilename(String path)
    {
        DataPath dataPath = new DataPath(path);

        if (dataPath.startsWith(this.path, 1))
        {
            return dataPath;
        }

        return null;
    }

    public void setCurrentFile(String path)
    {
        if (path == null)
        {
            return;
        }

        DataPath dataPath = new DataPath(path);

        if (dataPath.strings.size() == 1)
        {
            this.goTo(DataPath.EMPTY);
            this.setCurrentScroll(dataPath);
        }
        else
        {
            this.goTo(dataPath.getParent());
            this.setCurrentScroll(dataPath);
        }
    }

    /* UIList overrides */

    @Override
    protected boolean sortElements()
    {
        this.list.sort(Comparator.comparing(DataPath::toString));

        return true;
    }

    @Override
    protected void renderElementPart(UIContext context, DataPath element, int i, int x, int y, boolean hover, boolean selected)
    {
        context.batcher.icon(element.folder ? Icons.FOLDER : this.fileIcon, x, y);

        super.renderElementPart(context, element, i, x + 12, y, hover, selected);
    }

    @Override
    protected String elementToString(int i, DataPath element)
    {
        return element.getLast() + (element.folder ? "/" : "");
    }
}