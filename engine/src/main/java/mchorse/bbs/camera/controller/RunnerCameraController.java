package mchorse.bbs.camera.controller;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.ui.film.UIFilmPanel;

public class RunnerCameraController extends CameraWorkCameraController
{
    public int ticks;

    private float lastTransition = 0;
    private Position manual;
    private UIFilmPanel panel;

    public RunnerCameraController(IBridge bridge, UIFilmPanel panel)
    {
        super(bridge);

        this.panel = panel;
        this.context.playing = false;
    }

    public boolean isRunning()
    {
        return this.context.playing;
    }

    public void toggle(int ticks)
    {
        this.context.playing = !this.context.playing;
        this.ticks = ticks;
    }

    public void setManual(Position manual)
    {
        this.manual = manual;

        if (manual != null)
        {
            manual.copy(this.position);
        }
    }

    @Override
    public void update()
    {
        if (this.context.playing && this.manual == null)
        {
            this.ticks += 1;

            if (this.ticks >= this.context.clips.calculateDuration())
            {
                this.context.playing = false;
            }
        }
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        if (this.context.playing)
        {
            this.lastTransition = transition;
        }

        if (this.manual != null)
        {
            this.manual.apply(camera);
        }
        else if (this.context.clips != null)
        {
            this.apply(camera, this.ticks, this.context.playing ? transition : this.lastTransition);
        }

        this.panel.getController().handleCamera(camera, transition);
    }
}