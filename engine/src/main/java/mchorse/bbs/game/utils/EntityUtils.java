package mchorse.bbs.game.utils;

import mchorse.bbs.game.entities.components.NpcComponent;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.states.States;
import mchorse.bbs.world.entities.Entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntityUtils
{
    public static final Set<String> ENTITY_PROPERTIES = new HashSet<String>(Arrays.asList("x", "y", "z", "mx", "my", "mz", "grounded"));

    public static boolean isPlayer(Entity entity)
    {
        return entity != null && entity.has(PlayerComponent.class);
    }

    public static boolean isNpc(Entity entity)
    {
        return entity != null && entity.has(NpcComponent.class);
    }

    public static States getStates(Entity entity)
    {
        if (entity == null)
        {
            return null;
        }

        PlayerComponent character = entity.get(PlayerComponent.class);

        return character == null ? null : character.states;
    }

    public static double getProperty(Entity entity, String property)
    {
        switch (property)
        {
            case "x": return entity.basic.position.x;
            case "y": return entity.basic.position.y;
            case "z": return entity.basic.position.z;
            case "mx": return entity.basic.velocity.x;
            case "my": return entity.basic.velocity.y;
            case "mz": return entity.basic.velocity.z;
            case "grounded": return entity.basic.grounded ? 1 : 0;
        }

        return 0;
    }
}