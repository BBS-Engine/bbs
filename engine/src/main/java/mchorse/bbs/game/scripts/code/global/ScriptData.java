package mchorse.bbs.game.scripts.code.global;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.DoubleType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.game.scripts.user.global.IScriptData;

public class ScriptData implements IScriptData
{
    @Override
    public MapType map(String data)
    {
        MapType map = DataToString.mapFromString(data);

        return map == null ? new MapType() : map;
    }

    @Override
    public MapType mapFromJS(Object jsObject)
    {
        BaseType base = this.convertToData(jsObject);

        return base instanceof MapType ? (MapType) base : null;
    }

    @Override
    public ListType list(String string)
    {
        ListType list = DataToString.listFromString(string);

        return list == null ? new ListType() : list;
    }

    @Override
    public ListType listFromJS(Object jsObject)
    {
        BaseType base = this.convertToData(jsObject);

        return base instanceof ListType ? (ListType) base : null;
    }

    private BaseType convertToData(Object object)
    {
        if (object instanceof String)
        {
            return new StringType((String) object);
        }
        else if (object instanceof Double)
        {
            return new DoubleType((Double) object);
        }
        else if (object instanceof Integer)
        {
            return new IntType((Integer) object);
        }
        else if (object instanceof ScriptObjectMirror)
        {
            ScriptObjectMirror mirror = (ScriptObjectMirror) object;

            if (mirror.isArray())
            {
                ListType list = new ListType();

                for (int i = 0, c = mirror.size(); i < c; i++)
                {
                    BaseType base = this.convertToData(mirror.getSlot(i));

                    if (base != null)
                    {
                        list.add(base);
                    }
                }

                return list;
            }
            else
            {
                MapType map = new MapType();

                for (String key : mirror.keySet())
                {
                    BaseType base = this.convertToData(mirror.get(key));

                    if (base != null)
                    {
                        map.put(key, base);
                    }
                }

                return map;
            }
        }

        return null;
    }
}