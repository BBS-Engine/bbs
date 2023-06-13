package mchorse.bbs.game.regions.shapes;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3d;
import org.joml.Vector3i;

/**
 * Abstract shape class
 *
 * This base class provides base of operation for region's shapes
 */
public abstract class Shape implements IMapSerializable
{
    public Vector3d pos = new Vector3d();

    public void copyFrom(Shape shape)
    {
        this.pos.set(shape.pos);
    }

    public boolean isPlayerInside(Entity player, Vector3i tile)
    {
        if (this.pos == null)
        {
            return false;
        }

        return this.isPlayerInside(player.basic.position.x, (player.basic.position.y + player.basic.hitbox.h / 2), player.basic.position.z, tile);
    }

    public boolean isPlayerInside(double x, double y, double z, Vector3i tile)
    {
        if (this.pos == null)
        {
            return false;
        }

        return this.isInside(x - tile.x - 0.5, y - tile.y - 0.5, z - tile.z - 0.5);
    }

    public abstract boolean isInside(double x, double y, double z);

    @Override
    public void toData(MapType data)
    {
        data.putDouble("x", this.pos.x);
        data.putDouble("y", this.pos.y);
        data.putDouble("z", this.pos.z);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("x") && data.has("y") && data.has("z"))
        {
            this.pos = new Vector3d(
                data.getDouble("x"),
                data.getDouble("y"),
                data.getDouble("z")
            );
        }
    }
}