package mchorse.bbs.world.entities.architect.blueprints;

import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.recording.RecordComponent;
import mchorse.bbs.world.entities.EntityRecord;
import mchorse.bbs.world.entities.components.FormComponent;

import java.util.List;

public class PlayerEntityBlueprint extends BasicEntityBlueprint
{
    @Override
    public void fillComponents(List<EntityRecord> records)
    {
        super.fillComponents(records);

        records.add(new EntityRecord(FormComponent.class, new FormComponent()));
        records.add(new EntityRecord(PlayerComponent.class, new PlayerComponent()));
        records.add(new EntityRecord(RecordComponent.class, new RecordComponent()));
    }
}