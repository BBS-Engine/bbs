package mchorse.bbs.world.objects;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.world.IWorldObject;
import mchorse.bbs.world.World;
import org.joml.Vector3d;

import java.util.List;

public abstract class WorldObject implements IMapSerializable, IWorldObject
{
    public String id = "";
    public final Vector3d position = new Vector3d();

    private final AABB pickingHitbox = new AABB();

    public void addCollisionBoxes(List<AABB> boxes)
    {}

    @Override
    public AABB getPickingHitbox()
    {
        return this.pickingHitbox.setPosition(this.position.x, this.position.y, this.position.z).setSize(1, 1, 1);
    }

    public abstract void update(World world);

    public void render(RenderingContext context)
    {
        if (context.isDebug())
        {
            this.renderDebug(context);
        }
    }

    public void renderDebug(RenderingContext context)
    {
        AABB aabb = this.getPickingHitbox();

        Draw.renderBox(context, aabb.x, aabb.y, aabb.z, aabb.w, aabb.h, aabb.d);
    }

    @Override
    public String toString()
    {
        return "(" + (int) this.position.x + ", " + (int) this.position.y + ", " + (int) this.position.z + ")";
    }

    @Override
    public void toData(MapType data)
    {
        data.put("position", DataStorageUtils.vector3dToData(this.position));
        data.putString("id", this.id);
    }

    @Override
    public void fromData(MapType data)
    {
        this.position.set(DataStorageUtils.vector3dFromData(data.getList("position")));
        this.id = data.getString("id");
    }
}