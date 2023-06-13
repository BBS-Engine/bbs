package mchorse.bbs.world.objects;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.misc.hotkeys.TriggerHotkeys;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class TriggerObject extends WorldObject
{
    public TriggerHotkeys hotkeys = new TriggerHotkeys();
    public Vector3f hitbox = new Vector3f(1, 1, 1);

    @Override
    public AABB getPickingHitbox()
    {
        AABB hitbox = super.getPickingHitbox();

        hitbox.x -= this.hitbox.x / 2;
        hitbox.z -= this.hitbox.z / 2;
        hitbox.w = this.hitbox.x;
        hitbox.h = this.hitbox.y;
        hitbox.d = this.hitbox.z;

        return hitbox;
    }

    @Override
    public void update(World world)
    {}

    @Override
    public String toString()
    {
        return super.toString() + " " + this.hotkeys.hotkeys.size() + " hotkeys";
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        this.hotkeys.toData(data);
        data.put("hitbox", DataStorageUtils.vector3fToData(this.hitbox));
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.hotkeys.fromData(data);
        this.hitbox.set(DataStorageUtils.vector3fFromData(data.getList("hitbox"), new Vector3f(1, 1, 1)));
    }
}