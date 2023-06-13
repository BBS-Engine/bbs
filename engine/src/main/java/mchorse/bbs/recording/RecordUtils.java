package mchorse.bbs.recording;

import mchorse.bbs.world.entities.Entity;

public class RecordUtils
{
    public static void setPlayer(Entity entity, RecordPlayer player)
    {
        RecordComponent component = entity.get(RecordComponent.class);

        if (component != null)
        {
            component.player = player;
        }
    }

    public static void setRecorder(Entity entity, RecordRecorder recorder)
    {
        RecordComponent component = entity.get(RecordComponent.class);

        if (component != null)
        {
            component.recorder = recorder;
        }
    }
}