package mchorse.bbs.ui.film.clips.renderer;

import mchorse.bbs.audio.Waveform;
import mchorse.bbs.camera.clips.misc.VoicelineClip;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;

public class UIVoicelineClipRenderer extends UIClipRenderer<VoicelineClip>
{
    @Override
    protected void renderBackground(UIContext context, int color, VoicelineClip clip, Area area, boolean selected, boolean current)
    {
        Waveform waveform = UIFilmPanel.getVoiceLines().get(clip).b;

        if (waveform != null)
        {
            context.batcher.box(area.x, area.y, area.ex(), area.ey(), Colors.mulRGB(color, 0.6F));
            waveform.render(context.batcher, Colors.WHITE, area.x, area.y, area.w, area.h, 0, clip.duration.get() / 20F);
        }
        else
        {
            super.renderBackground(context, color, clip, area, selected, current);
        }
    }
}