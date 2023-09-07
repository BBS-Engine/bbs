package mchorse.bbs.film.replays;

import mchorse.bbs.settings.values.ValueList;

public class Replays extends ValueList<Replay>
{
    public Replays(String id)
    {
        super(id);
    }

    public Replay addReplay()
    {
        Replay replay = new Replay(String.valueOf(this.list.size()));

        this.list.add(replay);
        this.add(replay);

        return replay;
    }

    public void remove(Replay replay)
    {
        this.list.remove(replay);

        this.sync();
    }

    @Override
    protected Replay create(String id)
    {
        return new Replay(id);
    }
}