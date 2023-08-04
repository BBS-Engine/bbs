package mchorse.bbs.recording.scene;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.clips.CameraClip;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueString;

public class SceneClip extends CameraClip
{
    public ValueString scene = new ValueString("scene", "");
    public ValueInt offset = new ValueInt("offset", 0);

    public SceneClip()
    {
        this.register(this.scene);
        this.register(this.offset);
    }

    @Override
    public boolean isGlobal()
    {
        return true;
    }

    @Override
    public void shutdown(ClipContext context)
    {
        super.shutdown(context);

        if (this.scene.get().isEmpty())
        {
            return;
        }

        Scene scene = BBSData.getScenes().get(this.scene.get(), context.bridge.get(IBridgeWorld.class).getWorld());

        if (scene != null)
        {
            scene.stopPlayback();
        }
    }

    @Override
    protected void applyClip(ClipContext context, Position position)
    {
        if (this.scene.get().isEmpty())
        {
            return;
        }

        Scene scene = BBSData.getScenes().get(this.scene.get(), context.bridge.get(IBridgeWorld.class).getWorld());

        if (scene == null)
        {
            return;
        }

        int tick = context.relativeTick;

        if (tick < 0 || tick >= this.duration.get())
        {
            scene.stopPlayback();
        }
        else
        {
            tick += this.offset.get();

            if (context.playing)
            {
                if (scene.actors.isEmpty())
                {
                    scene.spawn(tick);
                    scene.resume(tick);
                }
                else
                {
                    scene.resume(tick);
                }
            }
            else
            {
                if (scene.playing)
                {
                    scene.pause();
                }

                if (scene.actors.isEmpty())
                {
                    scene.spawn(tick);
                }
                else
                {
                    scene.goTo(tick, true);
                }
            }
        }
    }

    @Override
    protected Clip create()
    {
        return new SceneClip();
    }
}