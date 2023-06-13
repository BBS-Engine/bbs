package mchorse.bbs.recording.scene;

import mchorse.bbs.BBS;
import mchorse.bbs.audio.SoundPlayer;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.settings.values.ValueLink;
import mchorse.bbs.resources.Link;

public class AudioClip extends Clip
{
    public ValueLink audio = new ValueLink("audio", null);

    public AudioClip()
    {
        super();

        this.register(this.audio);
    }

    @Override
    public void shutdown(ClipContext context)
    {
        Link link = this.audio.get();

        if (link != null)
        {
            BBS.getSounds().stop(link);
        }
    }

    @Override
    protected void applyClip(ClipContext context, Position position)
    {
        Link link = this.audio.get();

        if (link != null)
        {
            SoundPlayer player = BBS.getSounds().playUnique(link);

            if (player == null)
            {
                return;
            }

            player.setRelative(true);

            float tickTime = (context.relativeTick + context.transition) / 20F;
            float time = player.getPlaybackPosition();

            if (player.isStopped())
            {
                player.setPlaybackPosition(0);
                player.play();
            }

            if (player.isPlaying() && !context.playing)
            {
                player.pause();
            }
            else if (player.isPaused() && context.playing)
            {
                player.play();
            }

            float diff = Math.abs(tickTime - time);

            if (diff > 0.05F)
            {
                player.setPlaybackPosition(tickTime);
            }
        }
    }

    @Override
    protected Clip create()
    {
        return new AudioClip();
    }
}