package mchorse.bbs.world.objects;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.regions.Region;
import mchorse.bbs.game.regions.RegionRenderer;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RegionObject extends WorldObject
{
    public Region region = new Region();

    private Set<UUID> players = new HashSet<UUID>(10);
    private Map<UUID, IntType> delays = new HashMap<UUID, IntType>();
    private int tick;

    private RegionRenderer renderer = new RegionRenderer();

    public void set(MapType data)
    {
        this.region = new Region();
        this.region.fromData(data);
    }

    private Vector3i getPos()
    {
        return new Vector3i((int) this.position.x, (int) this.position.y, (int) this.position.z);
    }

    @Override
    public AABB getPickingHitbox()
    {
        AABB hitbox = super.getPickingHitbox();

        hitbox.x -= hitbox.w / 2;
        hitbox.z -= hitbox.d / 2;

        return hitbox;
    }

    @Override
    public void update(World world)
    {
        if (!this.delays.isEmpty())
        {
            this.checkDelays(world);
        }

        int frequency = Math.max(this.region.update, 1);

        if (this.tick % frequency == 0)
        {
            this.checkRegion(world);
        }

        this.tick += 1;
    }

    private void checkDelays(World world)
    {
        Iterator<Map.Entry<UUID, IntType>> it = this.delays.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry<UUID, IntType> trigger = it.next();
            int delay = trigger.getValue().intValue();

            if (delay <= 0)
            {
                UUID id = trigger.getKey();
                Entity player = world.getEntityByUUID(id);

                if (player != null)
                {
                    this.region.triggerEnter(player, this.getPos());
                }

                it.remove();
            }

            trigger.getValue().value = delay - 1;
        }
    }

    private void checkRegion(World world)
    {
        for (Entity player : world.entities)
        {
            if (!EntityUtils.isPlayer(player))
            {
                continue;
            }

            boolean enabled = this.region.isEnabled(player);

            if (!this.region.passable)
            {
                enabled = !enabled;
            }

            UUID id = player.getUUID();
            boolean wasInside = this.players.contains(id);

            if (this.region.isPlayerInside(player, this.getPos()))
            {
                if (!enabled)
                {
                    continue;
                }

                if (!this.region.passable)
                {
                    this.handlePassing(player);
                }
                else if (!wasInside)
                {
                    if (this.region.delay > 0)
                    {
                        this.delays.put(id, new IntType(this.region.delay));
                    }
                    else
                    {
                        this.region.triggerEnter(player, this.getPos());
                    }

                    this.players.add(id);
                }
            }
            else if (wasInside)
            {
                if (this.delays.containsKey(id))
                {
                    this.delays.remove(id);
                }
                else
                {
                    this.region.triggerExit(player, this.getPos());
                }

                this.players.remove(id);
            }
        }
    }

    private void handlePassing(Entity player)
    {
        Vector3d last = new Vector3d(player.basic.prevPosition);

        if (last != null && !this.region.isPlayerInside(last.x, last.y + player.basic.hitbox.h / 2, last.z, this.getPos()))
        {
            this.teleportPlayer(player, last, last);

            return;
        }

        last = last.sub(player.basic.position);

        if (last.lengthSquared() > 0)
        {
            last = last.normalize();
            last = last.mul(-0.5D);

            double x = player.basic.position.x;
            double y = player.basic.position.y;
            double z = player.basic.position.z;

            while (this.region.isPlayerInside(player, this.getPos()))
            {
                player.basic.position.x += last.x;
                player.basic.position.y += last.y;
                player.basic.position.z += last.z;
            }

            last = new Vector3d(x, y, z);

            player.basic.position.x = x;
            player.basic.position.y = y;
            player.basic.position.z = z;

            this.teleportPlayer(player, last, last);
        }
    }

    private void teleportPlayer(Entity player, Vector3d vec, Vector3d last)
    {
        player.setPosition(vec.x, vec.y, vec.z);

        Vector3d velocity = last.sub(player.basic.position).mul(-0.5);

        if (velocity.distanceSquared(new Vector3d(0, 0, 0)) < 0.5 * 0.5)
        {
            velocity = velocity.normalize();
        }

        double y = Math.abs(velocity.y) < 0.01 ? 0.2 : velocity.y;

        player.basic.velocity.set((float) velocity.x, (float) y, (float) velocity.z);
    }

    @Override
    public void renderDebug(RenderingContext context)
    {
        this.renderer.render(context, this.region, this.position);
    }

    @Override
    public String toString()
    {
        Region region = this.region;
        String first = region.shapes.isEmpty() ? "" : " " + UIKeys.C_SHAPE.get(BBS.getFactoryShapes().getType(region.shapes.get(0))).get();

        return super.toString() + first;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("region", this.region.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("region"))
        {
            this.region.fromData(data.getMap("region"));
        }
    }
}