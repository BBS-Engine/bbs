package mchorse.bbs.film;

import mchorse.bbs.BBS;
import mchorse.bbs.film.replays.Replays;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.utils.clips.Clips;

public class Film extends ValueGroup
{
    public final Clips camera = new Clips("camera", BBS.getFactoryCameraClips());
    public final Replays replays = new Replays("replays");
    public final Clips voiceLines = new Clips("voice_lines", BBS.getFactoryScreenplayClips());

    public Film()
    {
        super("");

        this.add(this.camera);
        this.add(this.replays);
        this.add(this.voiceLines);
    }
}