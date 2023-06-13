package mchorse.bbs.game.scripts.code;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSData;
import mchorse.bbs.audio.SoundPlayer;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.misc.WorldForm;
import mchorse.bbs.game.scripts.code.entities.ScriptEntity;
import mchorse.bbs.game.scripts.user.IScriptBlockVariant;
import mchorse.bbs.game.scripts.user.IScriptRayTrace;
import mchorse.bbs.game.scripts.user.IScriptWorld;
import mchorse.bbs.game.scripts.user.entities.IScriptEntity;
import mchorse.bbs.game.states.States;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.objects.WorldObject;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptWorld implements IScriptWorld
{
    public static final int MAX_VOLUME = 100;

    private World world;

    public ScriptWorld(World world)
    {
        this.world = world;
    }

    @Override
    public World getRawWorld()
    {
        return this.world;
    }

    @Override
    public void setBlock(int x, int y, int z, IScriptBlockVariant block)
    {
        this.world.chunks.setBlock(x, y, z, block.getBlockVariant());
    }

    @Override
    public IScriptBlockVariant getBlock(int x, int y, int z)
    {
        return new ScriptBlockVariant(this.world.chunks.getBlock(x, y, z));
    }

    @Override
    public IScriptRayTrace rayTrace(double x, double y, double z, float dx, float dy, float dz, double maxDistance)
    {
        return ScriptRayTrace.traceFromWorld(this.world, new Vector3d(x, y, z), new Vector3f(dx, dy, dz).normalize(), false, maxDistance);
    }

    @Override
    public IScriptRayTrace rayTraceBlock(double x, double y, double z, float dx, float dy, float dz, double maxDistance)
    {
        return ScriptRayTrace.traceFromWorld(this.world, new Vector3d(x, y, z), new Vector3f(dx, dy, dz).normalize(), true, maxDistance);
    }

    @Override
    public WorldObject getObject(String id)
    {
        for (WorldObject object : this.world.objects)
        {
            if (object.id.equals(id))
            {
                return object;
            }
        }

        return null;
    }

    @Override
    public List<WorldObject> getObjects(String id)
    {
        return this.world.getObjects(id);
    }

    @Override
    public List<WorldObject> getAllObjects()
    {
        return Collections.unmodifiableList(this.world.objects);
    }

    @Override
    public IScriptEntity spawnEntity(String id, double x, double y, double z, MapType data)
    {
        Entity entity = this.world.architect.create(Link.create(id), data);

        entity.setPosition(x, y, z);
        this.world.addEntitySafe(entity);

        return ScriptEntity.create(entity);
    }

    @Override
    public IScriptEntity dropItem(ItemStack stack, double x, double y, double z)
    {
        Entity entity = this.world.dropItem(stack, x, y, z);

        return ScriptEntity.create(entity);
    }

    @Override
    public List<IScriptEntity> getEntities(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        List<IScriptEntity> entities = new ArrayList<IScriptEntity>();

        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxX = Math.max(x1, x2);
        double maxY = Math.max(y1, y2);
        double maxZ = Math.max(z1, z2);

        if (maxX - minX > MAX_VOLUME || maxY - minY > MAX_VOLUME || maxZ - minZ > MAX_VOLUME)
        {
            return entities;
        }

        for (Entity entity : this.world.getEntitiesInAABB(AABB.fromTwoPoints(minX, minY, minZ, maxX, maxY, maxZ)))
        {
            entities.add(ScriptEntity.create(entity));
        }

        return entities;
    }

    @Override
    public List<IScriptEntity> getEntities(double x, double y, double z, double radius)
    {
        radius = Math.abs(radius);
        List<IScriptEntity> entities = new ArrayList<IScriptEntity>();

        if (radius > MAX_VOLUME / 2)
        {
            return entities;
        }

        double minX = x - radius;
        double minY = y - radius;
        double minZ = z - radius;
        double maxX = x + radius;
        double maxY = y + radius;
        double maxZ = z + radius;

        for (Entity entity : this.world.getEntitiesInAABB(AABB.fromTwoPoints(minX, minY, minZ, maxX, maxY, maxZ)))
        {
            AABB box = entity.basic.hitbox;
            double eX = box.x + box.w / 2D;
            double eY = box.y + box.h / 2D;
            double eZ = box.z + box.d / 2D;

            double dX = x - eX;
            double dY = y - eY;
            double dZ = z - eZ;

            if (dX * dX + dY * dY + dZ * dZ < radius * radius)
            {
                entities.add(ScriptEntity.create(entity));
            }
        }

        return entities;
    }

    @Override
    public IScriptEntity getEntityByUUID(String uuid)
    {
        Entity entity = this.world.getEntityByUUID(uuid);

        return ScriptEntity.create(entity);
    }

    @Override
    public List<IScriptEntity> getAllEntities()
    {
        return this.world.entities.stream().map(ScriptEntity::create).collect(Collectors.toList());
    }

    @Override
    public void playSound(String event, double x, double y, double z, float volume, float pitch)
    {
        SoundPlayer player = BBS.getSounds().play(LinkUtils.create(event));

        player.setRelative(false);
        player.setVolume(volume);
        player.setPitch(pitch);
        player.setPosition((float) x, (float) y, (float) z);
        player.setVelocity(0, 0, 0);
        player.play();
    }

    @Override
    public States getStates()
    {
        return BBSData.getStates();
    }

    @Override
    public void displayForm(Form form, int expiration, double x, double y, double z, float yaw, float pitch)
    {
        if (form == null)
        {
            return;
        }

        WorldForm worldForm = new WorldForm();

        worldForm.form = FormUtils.copy(form);
        worldForm.expiration = expiration;
        worldForm.position.set(x, y, z);
        worldForm.rotation.set(pitch, yaw, 0);

        this.world.worldForms.add(worldForm);
    }
}