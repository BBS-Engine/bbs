package mchorse.bbs.ui.game.scripts.utils.documentation;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;

import java.util.ArrayList;
import java.util.List;

public class DocClass extends DocEntry
{
    public List<DocMethod> methods = new ArrayList<DocMethod>();

    @Override
    public List<DocEntry> getEntries()
    {
        List<DocEntry> entries = new ArrayList<DocEntry>();

        entries.addAll(this.methods);

        return entries;
    }

    public DocMethod getMethod(String name)
    {
        for (DocMethod method : this.methods)
        {
            if (method.name.equals(name))
            {
                return method;
            }
        }

        return null;
    }

    public void setup()
    {
        this.methods.removeIf(method -> method.annotations.contains("mchorse.bbs.bbs.scripts.ui.utils.DiscardMethod"));

        for (DocMethod method : this.methods)
        {
            method.parent = this;
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("methods", BaseType.TYPE_LIST))
        {
            for (BaseType base : data.getList("methods"))
            {
                DocMethod method = new DocMethod();

                method.fromData(base.asMap());
                this.methods.add(method);
            }
        }
    }
}