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

        this.preNotifyParent();
        this.add(replay);
        this.postNotifyParent();

        return replay;
    }

    public void remove(Replay replay)
    {
        this.preNotifyParent();
        this.list.remove(replay);
        this.postNotifyParent();

        this.sync();
    }

    @Override
    protected Replay create(String id)
    {
        return new Replay(id);
    }
}