package mchorse.bbs.camera.clips.misc;

import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.utils.clips.Clip;

import java.util.UUID;

public class VoicelineClip extends Clip
{
    public final ValueString uuid = new ValueString("uuid", "");
    public final ValueString content = new ValueString("text", "");
    public final ValueString voice = new ValueString("voice", "");
    public final ValueString variant = new ValueString("variant", "");

    public VoicelineClip()
    {
        super();

        this.uuid.set(UUID.randomUUID().toString());

        this.add(this.uuid);
        this.add(this.content);
        this.add(this.voice);
        this.add(this.variant);
    }

    @Override
    protected Clip create()
    {
        return new VoicelineClip();
    }
}