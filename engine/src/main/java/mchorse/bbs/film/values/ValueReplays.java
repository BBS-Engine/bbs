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

    public void add(ValueReplay replay)
    {
        this.replays.add(replay);
        this.add(replay);
    }

    public void sync()
    {
        this.removeAll();

        for (ValueReplay replay : this.replays)
        {
            this.add(replay);
        }
    }
}