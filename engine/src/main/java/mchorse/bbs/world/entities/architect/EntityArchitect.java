package mchorse.bbs.world.entities.architect;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.EntityRecord;
import mchorse.bbs.world.entities.architect.blueprints.BasicEntityBlueprint;
import mchorse.bbs.world.entities.architect.blueprints.IEntityBlueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityArchitect
{
    private Map<Link, IEntityBlueprint> blueprints = new HashMap<>();

    public static Entity createDummy()
    {
        return createEntity(Link.bbs("dummy"), new BasicEntityBlueprint());
    }

    public static Entity createEntity(Link id, IEntityBlueprint blueprint)
    {
        return createEntity(id, blueprint, null);
    }

    public static Entity createEntity(Link id, IEntityBlueprint blueprint, MapType data)
    {
        if (blueprint == null)
        {
            return null;
        }

        List<EntityRecord> records = new ArrayList<>();

        blueprint.fillComponents(records);
        records.sort(Comparator.comparingInt(a -> a.index));

        Entity entity = new Entity(id, records);

        blueprint.setupEntity(entity);

        if (data != null)
        {
            entity.fromData(data);
        }

        return entity;
    }

    public void register(Link id, IEntityBlueprint blueprint)
    {
        this.blueprints.put(id, blueprint);
    }

    public Entity create(MapType data)
    {
        return create(Link.create(data.getString("id")), data);
    }

    public Entity create(Link id)
    {
        return create(id, null);
    }

    public Entity create(Link id, MapType data)
    {
        if (id == null)
        {
            return null;
        }

        IEntityBlueprint blueprint = this.blueprints.get(id);

        return createEntity(id, blueprint, data);
    }

    public Set<Link> getKeys()
    {
        return Collections.unmodifiableSet(this.blueprints.keySet());
    }
}