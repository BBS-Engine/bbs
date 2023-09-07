package mchorse.bbs.film.replays;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.ValueGroup;

import java.util.ArrayList;
import java.util.List;

public class Replays extends ValueGroup
{
    public final List<Replay> replays = new ArrayList<>();

    public Replays(String id)
    {
        super(id);
    }

    public Replay add()
    {
        Replay replay = new Replay(String.valueOf(this.replays.size()));

        this.replays.add(replay);
        this.add(replay);

        return replay;
    }

    public void remove(Replay replay)
    {
        this.replays.remove(replay);

        this.sync();
    }

    public void sync()
    {
        this.removeAll();

        int i = 0;

        for (Replay replay : this.replays)
        {
            replay.setId(String.valueOf(i));
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

            if (mapType.isEmpty())
            {
                continue;
            }

            this.add().fromData(mapType);
        }
    }
}