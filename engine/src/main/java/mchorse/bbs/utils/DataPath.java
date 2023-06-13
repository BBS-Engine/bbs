package mchorse.bbs.utils;

import java.util.ArrayList;
import java.util.List;

public class DataPath
{
    public static final DataPath EMPTY = new DataPath(true);

    public List<String> strings = new ArrayList<String>();
    public boolean folder;

    public DataPath(String path)
    {
        this.set(path);
    }

    public DataPath(boolean folder)
    {
        this.folder = folder;
    }

    public void set(String path)
    {
        this.strings.clear();

        if (path.isEmpty())
        {
            this.folder = true;

            return;
        }

        for (String string : path.split("/"))
        {
            if (string.trim().isEmpty())
            {
                continue;
            }

            this.strings.add(string);
        }

        this.folder = path.trim().endsWith("/");
    }

    public void combine(DataPath path)
    {
        this.strings.addAll(path.strings);

        this.folder = path.folder;
    }

    public DataPath copy()
    {
        DataPath dataPath = new DataPath(this.folder);

        dataPath.strings.addAll(this.strings);

        return dataPath;
    }

    public DataPath copy(DataPath path)
    {
        this.strings.clear();
        this.strings.addAll(path.strings);

        this.folder = path.folder;

        return this;
    }

    public DataPath getParent()
    {
        DataPath dataPath = this.copy();

        if (!dataPath.strings.isEmpty())
        {
            dataPath.strings.remove(dataPath.strings.size() - 1);
        }

        dataPath.folder = true;

        return dataPath;
    }

    public DataPath getChild(String name)
    {
        DataPath dataPath = this.copy();
        DataPath child = new DataPath(name);

        dataPath.combine(child);

        return dataPath;
    }

    public DataPath getTo(int levels)
    {
        DataPath dataPath = this.copy();

        while (dataPath.strings.size() > levels)
        {
            dataPath.strings.remove(dataPath.strings.size() - 1);
            dataPath.folder = true;
        }

        return dataPath;
    }

    public boolean startsWith(DataPath path, int levels)
    {
        if (this.startsWith(path))
        {
            return this.strings.size() - path.strings.size() == levels;
        }

        return false;
    }

    public boolean startsWith(DataPath path)
    {
        if (this.strings.size() < path.strings.size())
        {
            return false;
        }

        for (int i = 0, c = path.strings.size(); i < c; i++)
        {
            if (!this.strings.get(i).equals(path.strings.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof DataPath)
        {
            DataPath dataPath = (DataPath) obj;

            return this.toString().equals(dataPath.toString());
        }

        return false;
    }

    @Override
    public String toString()
    {
        return String.join("/", this.strings);
    }

    public String getLast()
    {
        if (this.strings.isEmpty())
        {
            return "";
        }

        return this.strings.get(this.strings.size() - 1);
    }

    @Override
    public int hashCode()
    {
        int code = 5;

        for (String string : this.strings)
        {
            code = 37 * code + string.hashCode();
        }

        return code;
    }
}