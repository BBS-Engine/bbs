package mchorse.bbs.film.values;

import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.world.entities.Entity;

import java.util.List;

public class ValueReplay extends ValueGroup
{
    public final ValueForm form = new ValueForm("form");
    public final ValueFrames keyframes = new ValueFrames("keyframes");

    public ValueReplay(String id)
    {
        super(id);

        this.add(this.form);
        this.add(this.keyframes);
    }

    void remapId(String id)
    {
        this.id = id;
    }

    public void applyFrame(int tick, Entity actor)
    {
        this.applyFrame(tick, actor, null);
    }

    public void applyFrame(int tick, Entity actor, List<String> groups)
    {
        this.keyframes.apply(tick, actor, groups);
    }
}