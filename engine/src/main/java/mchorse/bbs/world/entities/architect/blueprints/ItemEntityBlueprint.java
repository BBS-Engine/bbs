package mchorse.bbs.world.entities.architect.blueprints;

import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.EntityRecord;
import mchorse.bbs.world.entities.components.ItemComponent;

import java.util.List;

public class ItemEntityBlueprint extends BasicEntityBlueprint
{
    @Override
    public void fillComponents(List<EntityRecord> records)
    {
        super.fillComponents(records);

        records.add(new EntityRecord(ItemComponent.class, new ItemComponent()));
    }

    @Override
    public void setupEntity(Entity entity)
    {
        entity.basic.setHitboxSize(0.8F, 0.8F);
    }
}