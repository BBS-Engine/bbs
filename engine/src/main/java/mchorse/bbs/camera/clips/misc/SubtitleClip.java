package mchorse.bbs.camera.clips.misc;

import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class SubtitleClip extends Clip
{
    public ValueString label = new ValueString("label", "");
    public ValueInt x = new ValueInt("x", 0);
    public ValueInt y = new ValueInt("y", 0);
    public ValueFloat size = new ValueFloat("size", 1F);
    public ValueFloat anchorX = new ValueFloat("anchorX", 0F);
    public ValueFloat anchorY = new ValueFloat("anchorY", 0F);
    public ValueInt color = new ValueInt("color", 0xffffff);
    public ValueFloat windowX = new ValueFloat("windowX", 0F);
    public ValueFloat windowY = new ValueFloat("windowY", 0F);

    private Subtitle subtitle = new Subtitle();

    public static List<Subtitle> getSubtitles(ClipContext context)
    {
        Object object = context.clipData.get("subtitles");
        List<Subtitle> subtitles;

        if (object instanceof List)
        {
            subtitles = (List<Subtitle>) object;
        }
        else
        {
            subtitles = new ArrayList<Subtitle>();

            context.clipData.put("subtitles", subtitles);
        }

        return subtitles;
    }

    public SubtitleClip()
    {
        this.register(this.label);
        this.register(this.x);
        this.register(this.y);
        this.register(this.size);
        this.register(this.anchorX);
        this.register(this.anchorY);
        this.register(this.color);
        this.register(this.windowX);
        this.register(this.windowY);
    }

    @Override
    protected void applyClip(ClipContext context, Position position)
    {
        List<Subtitle> subtitles = getSubtitles(context);
        int color = Colors.setA(this.color.get(), this.envelope.get().factorEnabled(this.duration.get(), context.relativeTick + context.transition));

        this.subtitle.update(this.label.get(), this.x.get(), this.y.get(), this.size.get(), this.anchorX.get(), this.anchorY.get(), color);
        this.subtitle.updateWindow(this.windowX.get(), this.windowY.get());
        subtitles.add(this.subtitle);
    }

    @Override
    protected Clip create()
    {
        return new SubtitleClip();
    }
}