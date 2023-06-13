package mchorse.bbs.events.register;

import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.architect.EntityArchitect;

public class RegisterArchitectBlueprintsEvent
{
    public final World world;
    public final EntityArchitect architect;

    public RegisterArchitectBlueprintsEvent(World world)
    {
        this.world = world;
        this.architect = world.architect;
    }
}