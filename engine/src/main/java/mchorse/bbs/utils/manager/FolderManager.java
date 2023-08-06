package mchorse.bbs.utils.manager;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.utils.manager.data.AbstractData;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Folder based manager
 */
public abstract class FolderManager <T extends AbstractData> implements IManager<T>
{
    protected Map<String, ManagerCache> cache = new HashMap<>();
    protected File folder;
    protected long lastCheck;

    public FolderManager(File folder)
    {
        if (folder != null)
        {
            this.folder = folder;
            this.folder.mkdirs();
        }
    }

    public File getFolder()
    {
        return this.folder;
    }

    protected boolean canCache()
    {
        return BBSSettings.generalDataCaching.get();
    }

    protected void doExpirationCheck()
    {
        final int threshold = 1000 * 30;
        long current = System.currentTimeMillis();

        /* Check every 30 seconds all cached entries and remove those that weren't used in
         * last 30 seconds */
        if (current - this.lastCheck > threshold)
        {
            this.cache.values().removeIf((cache) -> current - cache.lastUsed > threshold);

            this.lastCheck = current;
        }
    }

    @Override
    public boolean exists(String name)
    {
        return this.getFile(name).exists();
    }

    @Override
    public boolean rename(String from, String to)
    {
        File file = this.getFile(from);

        if (file != null && file.exists())
        {
            if (file.renameTo(this.getFile(to)))
            {
                if (this.canCache())
                {
                    this.cache.put(to, this.cache.remove(from));
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean delete(String name)
    {
        File file = this.getFile(name);

        if (file != null && file.delete())
        {
            this.cache.remove(name);

            return true;
        }

        return false;
    }

    /**
     * Add a folder.
     */
    public boolean addFolder(String path)
    {
        File folder = this.getFolder(this.normalizePath(path));

        if (folder.exists())
        {
            return false;
        }

        return folder.mkdirs();
    }

    /**
     * Rename given folder to another name. From and to arguments expect trailing slashes!
     */
    public boolean renameFolder(String from, String to)
    {
        from = this.normalizePath(from);
        to = this.normalizePath(to);

        File folder = this.getFolder(from);

        if (folder.isDirectory())
        {
            if (folder.renameTo(this.getFolder(to)))
            {
                if (this.canCache())
                {
                    Set<String> keys = new HashSet<>(this.getKeys());

                    for (String key : keys)
                    {
                        if (!key.startsWith(from))
                        {
                            continue;
                        }

                        ManagerCache cache = this.cache.remove(key);

                        if (cache != null)
                        {
                            this.cache.put(to + key.substring(from.length()), cache);
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Delete given folder. It only works if the folder is empty.
     */
    public boolean deleteFolder(String path)
    {
        File folder = this.getFolder(this.normalizePath(path));

        if (folder.isDirectory())
        {
            if (folder.delete())
            {
                if (this.canCache())
                {
                    Set<String> keys = new HashSet<>(this.getKeys());

                    for (String key : keys)
                    {
                        if (key.startsWith(path))
                        {
                            this.cache.remove(key);
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    private String normalizePath(String path)
    {
        return path.endsWith("/") ? path : path + "/";
    }

    @Override
    public Collection<String> getKeys()
    {
        Set<String> set = new HashSet<>();

        if (this.folder == null)
        {
            return set;
        }

        this.recursiveFind(set, this.folder, "");

        return set;
    }

    private void recursiveFind(Set<String> set, File folder, String prefix)
    {
        for (File file : folder.listFiles())
        {
            String name = file.getName();

            if (file.isFile() && this.isData(file))
            {
                set.add(prefix + name.substring(0, name.lastIndexOf(".")));
            }
            else if (file.isDirectory())
            {
                File[] files = file.listFiles();

                if (files == null || files.length == 0)
                {
                    set.add(prefix + name + "/");
                }
                else
                {
                    this.recursiveFind(set, file, prefix + name + "/");
                }
            }
        }
    }

    protected boolean isData(File file)
    {
        return file.getName().endsWith(this.getExtension());
    }

    public File getFile(String name)
    {
        if (this.folder == null)
        {
            return null;
        }

        return new File(this.folder, name + this.getExtension());
    }

    public File getFolder(String path)
    {
        if (this.folder == null)
        {
            return null;
        }

        return new File(this.folder, path);
    }

    protected String getExtension()
    {
        return ".json";
    }
}