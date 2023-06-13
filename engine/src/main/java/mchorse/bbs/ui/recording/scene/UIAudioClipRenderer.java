package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.BBS;
import mchorse.bbs.audio.SoundBuffer;
import mchorse.bbs.recording.scene.AudioClip;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.camera.clips.renderer.UIClipRenderer;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;

public class UIAudioClipRenderer extends UIClipRenderer<AudioClip>
{
    @Override
    protected void renderBackground(UIContext context, int color, AudioClip clip, Area area, boolean compact, boolean selected, boolean current)
    {
        Link link = clip.audio.get();

        if (!compact && link != null)
        {
            SoundBuffer player = BBS.getSounds().get(link, true);

            if (player != null)
            {
                context.draw.box(area.x, area.y, area.ex(), area.ey(), Colors.mulRGB(color, 0.6F));
                player.getWaveform().render(context.draw, Colors.WHITE, area.x, area.y, area.w, area.h, 0, clip.duration.get() / 20F);
            }
        }
        else
        {
            super.renderBackground(context, color, clip, area, compact, selected, current);
        }
    }
}