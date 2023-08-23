package mchorse.bbs.film;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.film.values.ValueReplays;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.utils.clips.values.ValueClips;

public class Film extends StructureBase
{
    public final ValueInt length = new ValueInt("length", 0);
    public final ValueClips camera = new ValueClips("camera", BBS.getFactoryClips());
    public final ValueReplays replays = new ValueReplays("replays");

    public Film()
    {
        this.register(this.length);
        this.register(this.camera);
        this.register(this.replays);
    }
}