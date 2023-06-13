package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.BBS;
import mchorse.bbs.audio.SoundPlayer;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EnumUtils;
import mchorse.bbs.resources.Link;

public class SoundTriggerBlock extends StringTriggerBlock
{
    public PlayMode playMode = PlayMode.PLAY;

    public SoundTriggerBlock()
    {
        super();
    }

    @Override
    public void trigger(DataContext context)
    {
        if (this.playMode == PlayMode.PLAY && !this.id.isEmpty())
        {
            SoundPlayer player = BBS.getSounds().play(Link.create(this.id));

            player.setRelative(true);
        }
        else if (this.playMode == PlayMode.PLAY_WITH_WAVEFORM && !this.id.isEmpty())
        {
            SoundPlayer player = BBS.getSounds().playUnique(Link.create(this.id));

            player.setRelative(true);
        }
        else
        {
            BBS.getSounds().stop(Link.create(this.id));
        }
    }

    @Override
    protected String getKey()
    {
        return "sound";
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putInt("playMode", this.playMode.ordinal());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.playMode = EnumUtils.getValue(data.getInt("playMode"), PlayMode.values(), PlayMode.PLAY);
    }

    public static enum PlayMode
    {
        PLAY, STOP, PLAY_WITH_WAVEFORM;
    }
}