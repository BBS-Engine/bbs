package mchorse.bbs.film.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.ValueGroup;

import java.util.ArrayList;
import java.util.List;

public class ValueReplays extends ValueGroup
{
    public final List<ValueReplay> replays = new ArrayList<>();

    public ValueReplays(String id)
    {
        super(id);
    }

    public ValueReplay add()
    {
        ValueReplay replay = new ValueReplay(String.valueOf(this.replays.size()));

        this.replays.add(replay);
        this.add(replay);

        return replay;
    }

    public void remove(ValueReplay replay)
    {
        this.replays.remove(replay);

        this.sync();
    }

    public void sync()
    {
        this.removeAll();

        int i = 0;

        for (ValueReplay replay : this.replays)
        {
            replay.remapId(String.valueOf(i));
            this.add(replay);

            i += 1;
        }
    }

    @Override
    public void fromData(BaseType data)
    {
        super.fromData(data);

        this.replays.clear();

        if (!data.isMap())
        {
            return;
        }

        MapType map = data.asMap();

        for (String key : map.keys())
        {
            MapType mapType = map.getMap(key);

            if (!mapType.isEmpty())
            {
                ValueReplay add = this.add();

                add.fromData(mapType);
            }
        }
    }
}