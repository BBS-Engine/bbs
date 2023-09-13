package mchorse.bbs.film;

import mchorse.bbs.BBS;
import mchorse.bbs.film.replays.Replays;
import mchorse.bbs.film.screenplay.Screenplay;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.utils.clips.Clips;

import java.util.regex.Pattern;

public class Film extends ValueGroup
{
    public final Clips camera = new Clips("camera", BBS.getFactoryCameraClips());
    public final Replays replays = new Replays("replays");
    public final Screenplay screenplay = new Screenplay("screenplay");

    public Film()
    {
        super("");

        this.add(this.camera);
        this.add(this.replays);
        this.add(this.screenplay);
    }
}