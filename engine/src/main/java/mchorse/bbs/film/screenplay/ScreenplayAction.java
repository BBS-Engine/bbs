package mchorse.bbs.film.screenplay;

import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.ValueString;

import java.util.UUID;

public class ScreenplayAction extends ValueGroup
{
    public final ValueString uuid = new ValueString("uuid", "");
    public final ValueString content = new ValueString("content", "");
    public final ValueString voice = new ValueString("voice", "");
    public final ValueFloat pause = new ValueFloat("pause", 0F, -100F, 100F);
    public final ValueFloat cutoff = new ValueFloat("cutoff", 0F, -100F, 100F);
    public final ValueString variant = new ValueString("variant", "");

    public ScreenplayAction(String id)
    {
        super(id);

        this.uuid.set(UUID.randomUUID().toString());

        this.add(this.uuid);
        this.add(this.content);
        this.add(this.voice);
        this.add(this.pause);
        this.add(this.cutoff);
        this.add(this.variant);
    }
}