package mchorse.bbs.world.entities.architect.blueprints;

import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.EntityRecord;
import mchorse.bbs.world.entities.components.BasicComponent;
import mchorse.bbs.world.entities.components.CollisionComponent;

import java.util.List;

public class BasicEntityBlueprint implements IEntityBlueprint
{
    @Override
    public void fillComponents(List<EntityRecord> records)
    {
        records.add(new EntityRecord(BasicComponent.class, new BasicComponent()));
        records.add(new EntityRecord(CollisionComponent.class, new CollisionComponent()));
    }

    @Override
    public void setupEntity(Entity entity)
    {}
}