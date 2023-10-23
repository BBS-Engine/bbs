package mchorse.bbs.ui.film;

import mchorse.bbs.bridge.IBridgeVideoScreenshot;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.utils.recording.VideoRecorder;
import org.lwjgl.glfw.GLFW;

public class UIFilmRecorder extends UIElement
{
    public UIFilmPanel editor;

    private UIExit exit = new UIExit(this);
    private int end;

    public UIFilmRecorder(UIFilmPanel editor)
    {
        super();

        this.editor = editor;

        this.noCulling();
    }

    public boolean isRecording()
    {
        return this.getRecorder().isRecording();
    }

    private UIContext getUIContext()
    {
        return this.editor.getContext();
    }

    private VideoRecorder getRecorder()
    {
        return this.getUIContext().menu.bridge.get(IBridgeVideoScreenshot.class).getVideoRecorder();
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
            recorder.startRecording(framebuffer.getMainTexture());
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
        context.menu.getRoot().add(this.exit);
    }

    public void stop()
    {
        this.exit.removeFromParent();

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

    public static class UIExit extends UIElement
    {
        private UIFilmRecorder recorder;

        public UIExit(UIFilmRecorder recorder)
        {
            this.recorder = recorder;
        }

        @Override
        protected boolean subKeyPressed(UIContext context)
        {
            if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
            {
                this.recorder.stop();

                return true;
            }

            return super.subKeyPressed(context);
        }
    }
}