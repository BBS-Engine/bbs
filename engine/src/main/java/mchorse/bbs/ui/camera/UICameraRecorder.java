package mchorse.bbs.ui.camera;

import mchorse.bbs.bridge.IBridgeVideoRecorder;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.utils.recording.VideoRecorder;

public class UICameraRecorder extends UIElement
{
    public UICameraPanel editor;

    private boolean recording;
    private boolean waiting;
    private int end;

    public UICameraRecorder(UICameraPanel editor)
    {
        super();

        this.editor = editor;

        this.noCulling();
    }

    private VideoRecorder getRecorder()
    {
        return this.editor.getContext().menu.bridge.get(IBridgeVideoRecorder.class).getVideoRecorder();
    }

    public boolean isRecording()
    {
        return this.recording;
    }

    private boolean isRunning()
    {
        return this.editor.isRunning();
    }

    public void openMovies()
    {
        UIUtils.openFolder(this.getRecorder().movies);
    }

    public void startRecording()
    {
        VideoRecorder recorder = this.getRecorder();

        if (this.isRunning() || recorder.isRecording())
        {
            return;
        }

        /* Calculate start and end ticks */
        this.end = this.editor.getData().calculateDuration();

        if (this.end <= 0)
        {
            return;
        }

        try
        {
            recorder.startRecording(this.editor.getFramebuffer());
        }
        catch (Exception e)
        {
            UIMessageOverlayPanel panel = new UIMessageOverlayPanel(
                UIKeys.ERROR,
                IKey.str(e.getMessage())
            );

            UIOverlay.addOverlay(this.editor.getContext(), panel);

            return;
        }

        this.editor.timeline.setTickAndNotify(0);
        this.editor.dashboard.main.setEnabled(false);
        this.editor.dashboard.overlay.add(this);

        this.recording = this.waiting = true;
    }

    public void stop()
    {
        if (this.recording)
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

            this.editor.dashboard.main.setEnabled(true);
            this.editor.getContext().render.postRunnable(this::removeFromParent);

            this.recording = this.waiting = false;
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        this.minema(this.editor.isRunning() ? this.editor.getRunner().ticks : this.editor.timeline.tick, context.getTransition());
    }

    /**
     * Update the minema recording logic
     */
    public void minema(int ticks, float transition)
    {
        if (!this.recording)
        {
            return;
        }

        if (!this.getRecorder().isRecording())
        {
            this.stop();

            this.editor.getContext().render.postRunnable(() ->
            {
                UIMessageOverlayPanel panel = new UIMessageOverlayPanel(
                    UIKeys.ERROR,
                    UIKeys.CAMERA_RECORDING_PREMATURE_STOP
                );

                UIOverlay.addOverlay(this.editor.getContext(), panel);
            });

            return;
        }

        if (this.waiting)
        {
            if (!this.isRunning())
            {
                this.editor.togglePlayback();
                this.waiting = false;
            }
        }
        else
        {
            if (!this.isRunning() || ticks >= this.end)
            {
                this.stop();
            }
        }
    }
}