package mchorse.bbs.recording;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.world.entities.components.Component;

public class RecordComponent extends Component
{
    public RecordPlayer player;
    public RecordRecorder recorder;

    @Override
    public void entityWasRemoved()
    {
        super.entityWasRemoved();

        if (this.recorder != null)
        {
            BBSData.getRecords().halt(this.entity, true, true);

            this.entity.world.bridge.get(IBridgeWorld.class).sendMessage(RecordManager.EMPTY_FILENAME);
        }
    }

    @Override
    public void preUpdate()
    {
        super.preUpdate();

        if (this.player != null)
        {
            this.player.next();

            if (this.player.isFinished())
            {
                this.player.stopPlaying();
            }
        }
    }

    @Override
    public void postUpdate()
    {
        super.postUpdate();

        if (this.recorder != null)
        {
            this.recorder.record(this.entity);
        }
    }
}