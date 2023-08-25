package mchorse.studio.ui;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.audio.AudioRenderer;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.utils.colors.Colors;

public class UIHUD
{
    public UIScreen screen;

    private int fade = 20;

    public UIHUD(UIScreen screen)
    {
        this.screen = screen;
    }

    /* Rendering */

    public void postRenderHud(UIRenderingContext context, int w, int h)
    {
        if (!this.screen.hasMenu())
        {
            int aw = (int) (w * BBSSettings.audioWaveformWidth.get());
            int ah = BBSSettings.audioWaveformHeight.get();

            AudioRenderer.renderAll(context.batcher, (w - aw) / 2, 20, aw, ah, w, h);
        }

        if (this.fade < 0)
        {
            return;
        }

        context.batcher.box(0, 0, w, h, Colors.setA(Colors.WHITE, this.fade / 20F));

        this.fade -= 1;
    }

    public void update()
    {}
}