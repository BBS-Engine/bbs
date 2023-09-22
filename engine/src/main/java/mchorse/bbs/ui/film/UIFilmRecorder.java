package mchorse.bbs.ui.film;

import mchorse.bbs.bridge.IBridgeVideoRecorder;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.utils.recording.VideoRecorder;

public class UIFilmRecorder extends UIElement
{
    public UIFilmPanel editor;

    private int end;

    public UIFilmRecorder(UIFilmPanel editor)
    {
        super();

        this.editor = editor;

        this.noCulling();
    }

    private UIContext getUIContext()
    {
        return ((UIElement) this.editor).getContext();
    }

    private VideoRecorder getRecorder()
    {
        return this.getUIContext().menu.bridge.get(IBridgeVideoRecorder.class).getVideoRecorder();
    }

    private boolean isRunning()
    {
        return this.editor.isRunning();
    }

    public void openMovies()
    {
        UIUtils.openFolder(this.getRecorder().movies);
    }

    public void startRecording(int duration, Framebuffer framebuffer)
    {
        VideoRecorder recorder = this.getRecorder();
        UIContext context = this.getUIContext();

        if (this.isRunning() || recorder.isRecording() || duration <= 0)
        {
            return;
        }

        this.end = duration;

        try
        {
            recorder.startRecording(framebuffer);
        }
        catch (Exception e)
        {
            UIOverlay.addOverlay(context, new UIMessageOverlayPanel(UIKeys.GENERAL_ERROR, IKey.raw(e.getMessage())));

            return;
        }

        // this.editor.getController().createEntities();
        this.editor.setCursor(0);
        this.editor.togglePlayback();
        context.menu.main.setEnabled(false);
        context.menu.overlay.add(this);
    }

    public void stop()
    {
        if (this.getRecorder().isRecording())
        {
            try
            {
                this.getRecorder().stopRecording();
            }
            catch (Exception e) {}

            if (this.isRunning())
            {
                this.editor.togglePlayback();
            }

            UIContext context = this.getUIContext();

            context.menu.main.setEnabled(true);
            context.render.postRunnable(this::removeFromParent);
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        int ticks = this.editor.getCursor();

        if (!this.getRecorder().isRecording())
        {
            return;
        }

        if (!this.isRunning() || ticks >= this.end)
        {
            this.stop();
        }
    }
}