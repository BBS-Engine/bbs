package mchorse.bbs.film.values;

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

        this.sync();

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
}