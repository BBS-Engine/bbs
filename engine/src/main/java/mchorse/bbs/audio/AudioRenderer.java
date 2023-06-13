package mchorse.bbs.audio;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.ui.framework.elements.utils.UIDraw;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.colors.Colors;

public class AudioRenderer
{
    public static void renderAll(UIDraw draw, int x, int y, int w, int h, int sw, int sh)
    {
        if (!BBSSettings.audioWaveformVisible.get())
        {
            return;
        }

        for (SoundPlayer file : BBS.getSounds().getPlayers())
        {
            if (file.getBuffer().getWaveform() != null && !file.isStopped())
            {
                renderWaveform(draw, file, x, y, w, h, sw, sh);

                y -= h + 5;
            }
        }
    }

    public static void renderWaveform(UIDraw draw, SoundPlayer file, int x, int y, int w, int h, int sw, int sh)
    {
        if (file == null || file.getBuffer().getWaveform() == null)
        {
            return;
        }

        final float brightness = 0.45F;
        int half = w / 2;

        /* Draw background */
        draw.gradientVBox(x + 2, y + 2, x + w - 2, y + h, 0, Colors.A50);
        draw.box(x + 1, y, x + 2, y + h, 0xaaffffff);
        draw.box(x + w - 2, y, x + w - 1, y + h, 0xaaffffff);
        draw.box(x, y + h - 1, x + w, y + h, 0xffffffff);

        draw.clip(x + 2, y + 2, w - 4, h - 4, sw, sh);

        Waveform wave = file.getBuffer().getWaveform();

        if (!wave.isCreated())
        {
            wave.render();
        }

        float playback = file.getPlaybackPosition();
        int offset = (int) (playback * wave.getPixelsPerSecond());
        int waveW = wave.getWidth();

        /* Draw the waveform */
        int runningOffset = waveW - offset;

        if (runningOffset > 0)
        {
            wave.render2(draw, Colors.WHITE, x + half, y, offset, 0, Math.min(runningOffset, half), h, h);
        }

        /* Draw the passed waveform */
        if (offset > 0)
        {
            int xx = offset > half ? x : x + half - offset;
            int oo = offset > half ? offset - half : 0;
            int ww = offset > half ? half : offset;
            int color = Colors.COLOR.set(brightness, brightness, brightness, 1F).getARGBColor();

            wave.render2(draw, color, xx, y, oo, 0, ww, h, h);
        }

        draw.unclip(sw, sh);

        draw.box(x + half, y + 1, x + half + 1, y + h - 1, 0xff57f52a);

        FontRenderer fontRenderer = draw.context.getFont();

        if (BBSSettings.audioWaveformFilename.get())
        {
            draw.textCard(fontRenderer, file.getBuffer().getId().toString(), x + 8, y + h / 2 - 4, 0xffffff, 0x99000000);
        }

        if (BBSSettings.audioWaveformTime.get())
        {
            int tick = (int) Math.floor(playback * 20);
            int seconds = tick / 20;
            int milliseconds = (int) (tick % 20 == 0 ? 0 : tick % 20 * 5D);

            String tickLabel = tick + "t (" + seconds + "." + StringUtils.leftPad(String.valueOf(milliseconds), 2, "0") + "s)";

            draw.textCard(fontRenderer, tickLabel, x + w - 8 - fontRenderer.getWidth(tickLabel), y + h / 2 - 4, 0xffffff, 0x99000000);
        }
    }
}