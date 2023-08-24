package mchorse.bbs.world;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.events.register.RegisterArchitectBlueprintsEvent;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.voxel.generation.Generator;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.voxel.storage.ChunkArrayManager;
import mchorse.bbs.voxel.storage.ChunkFactory;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.ChunkStorage;
import mchorse.bbs.voxel.storage.ChunkView;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.tilesets.models.BlockModel;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;
import mchorse.bbs.world.entities.architect.blueprints.PlayerEntityBlueprint;
import mchorse.bbs.world.objects.WorldObject;
import org.greenrobot.eventbus.EventBus;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class World implements ITickable, IDisposable
{
    public List<WorldObject> objects = new ArrayList<>();
    public List<Entity> entities = new ArrayList<>();
    public Set<Entity> toAdd = new HashSet<>();
    public Set<Entity> toRemove = new HashSet<>();

    public String name;
    public final File folder;

    public ChunkStorage storage;
    public ChunkView view;
    public ChunkManager chunks;
    public Generator generator;

    public final IBridge bridge;

    public final EntityArchitect architect;
    public final WorldSettings settings = new WorldSettings();

    private EventBus eventBus;

    private File objectsFile;
    private File settingsFile;

    public World(IBridge bridge, ChunkFactory factory, Generator generator)
    {
        this.bridge = bridge;
        this.architect = this.createArchitect();

        this.eventBus = new EventBus();

        this.name = factory.getMetadata().name;
        this.folder = factory.folder;

        this.storage = factory.createStorage();
        this.chunks = factory.createManager();
        this.generator = generator;
    }

    protected EntityArchitect createArchitect()
    {
        EntityArchitect architect = new EntityArchitect();

        architect.register(Link.bbs("player"), new PlayerEntityBlueprint());

        BBS.events.post(new RegisterArchitectBlueprintsEvent(this));

        return architect;
    }

    public EventBus getEventBus()
    {
        return this.eventBus;
    }

    public void initialize(ChunkFactory factory)
    {
        this.generator.fromMetadata(factory.getMetadata(), factory.blocks);

        if (this.chunks instanceof ChunkArrayManager)
        {
            this.view = factory.createView((ChunkArrayManager) this.chunks, this);

            this.view.getThread().start();
        }
    }

    public void readExtraData(File save)
    {
        this.objectsFile = new File(save, "objects.json");

        if (this.objectsFile.exists())
        {
            try
            {
                this.objects.clear();

                String json = IOUtils.readText(this.objectsFile);
                MapType data = DataToString.mapFromString(json);
                ListType objectsList = data.getList("objects");

                for (BaseType objectType : objectsList)
                {
                    WorldObject object = BBS.getFactoryWorldObjects().fromData((MapType) objectType);

                    if (object != null)
                    {
                        this.objects.add(object);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.settingsFile = new File(save, "world.json");

        if (this.settingsFile.exists())
        {
            try
            {
                String json = IOUtils.readText(this.settingsFile);
                MapType data = DataToString.mapFromString(json);

                this.settings.fromData(data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete()
    {
        MapType props = new MapType();
        ListType propList = new ListType();

        for (WorldObject object : this.objects)
        {
            propList.add(BBS.getFactoryWorldObjects().toData(object));
        }

        props.put("objects", propList);

        try
        {
            IOUtils.writeText(this.objectsFile, DataToString.toString(props, true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            Camera camera = this.bridge.get(IBridgeCamera.class).getCamera();

            this.settings.cameraPosition.set(camera.position);
            this.settings.cameraRotation.set(camera.rotation);

            IOUtils.writeText(this.settingsFile, DataToString.toString(this.settings.toData(), true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.view.getThread().stop(true);
        this.chunks.delete();
    }

    public void addEntitySafe(Entity entity)
    {
        this.toAdd.add(entity);
        this.toRemove.remove(entity);
    }

    public void addEntity(Entity entity)
    {
        entity.setWorld(this);
        this.entities.add(entity);

        ChunkCell cell = this.getCellForEntity(entity);

        if (cell != null)
        {
            cell.addEntity(entity);
        }
    }

    public void removeEntitySafe(Entity entity)
    {
        this.toRemove.add(entity);
        this.toAdd.remove(entity);
    }

    public void removeEntity(Entity entity)
    {
        entity.setWorld(null);
        entity.remove();
        this.entities.remove(entity);

        ChunkCell cell = this.getCellForEntity(entity);

        if (cell != null)
        {
            cell.removeEntity(entity);
        }
    }

    @Override
    public void update()
    {
        this.updateEntities();
        this.updateCleanUp();
    }

    private void updateEntities()
    {
        for (Entity entity : this.entities)
        {
            if (entity.world != null)
            {
                ChunkCell lastCell = this.getCellForEntity(entity);

                entity.update();

                ChunkCell currentCell = this.getCellForEntity(entity);

                if (lastCell != currentCell)
                {
                    if (lastCell != null) lastCell.removeEntity(entity);
                    if (currentCell != null) currentCell.addEntity(entity);
                }

                if (currentCell == null)
                {
                    this.removeEntitySafe(entity);
                }
            }

            if (entity.isRemoved())
            {
                this.removeEntitySafe(entity);
            }
        }

        for (WorldObject object : this.objects)
        {
            object.update(this);
        }
    }

    private void updateCleanUp()
    {
        if (!this.toAdd.isEmpty())
        {
            for (Entity entity : this.toAdd)
            {
                this.addEntity(entity);
            }

            this.toAdd.clear();
        }

        if (!this.toRemove.isEmpty())
        {
            for (Entity entity : this.toRemove)
            {
                this.removeEntity(entity);
            }

            this.toRemove.clear();
        }
    }

    private ChunkCell getCellForEntity(Entity entity)
    {
        return this.chunks.getCell((int) entity.basic.position.x, (int) entity.basic.position.y, (int) entity.basic.position.z, false);
    }

    public Entity getEntityByUUID(String uuid)
    {
        return this.getEntityByUUID(UUID.fromString(uuid));
    }

    public Entity getEntityByUUID(UUID uuid)
    {
        for (Entity entity : this.entities)
        {
            if (entity.getUUID().equals(uuid))
            {
                return entity;
            }
        }

        return null;
    }

    public List<Entity> getEntitiesInAABB(AABB volume)
    {
        List<Entity> entities = new ArrayList<>();

        for (Entity entity : this.entities)
        {
            if (volume.intersects(entity.basic.hitbox))
            {
                entities.add(entity);
            }
        }

        return entities;
    }

    public List<AABB> getCollisionAABBs(AABB volume)
    {
        return this.getCollisionAABBs(volume.x, volume.y, volume.z, volume.w, volume.h, volume.d);
    }

    public List<AABB> getCollisionAABBs(double x, double y, double z, double w, double h, double d)
    {
        return this.getCollisionAABBs(x, y, z, w, h, d, 1, 1, 1);
    }

    public List<AABB> getCollisionAABBs(double x, double y, double z, double w, double h, double d, double dx, double dy, double dz)
    {
        List<AABB> boxes = new ArrayList<>();

        int originX = (int) Math.floor(Math.min(x, x + w));
        int originY = (int) Math.floor(Math.min(y, y + h));
        int originZ = (int) Math.floor(Math.min(z, z + d));

        int endX = (int) Math.ceil(Math.max(x, x + w));
        int endY = (int) Math.ceil(Math.max(y, y + h));
        int endZ = (int) Math.ceil(Math.max(z, z + d));

        /* Whether axes are positive */
        boolean px = dx >= 0;
        boolean py = dy >= 0;
        boolean pz = dz >= 0;

        for (int i = px ? originX : endX, fx = px ? 1 : -1; i >= originX && i <= endX; i += fx)
        {
            for (int j = py ? originY : endY, fy = py ? 1 : -1; j >= originY && j <= endY; j += fy)
            {
                for (int k = pz ? originZ : endZ, fz = pz ? 1 : -1; k >= originZ && k <= endZ; k += fz)
                {
                    BlockModel blockModel = this.chunks.getBlock(i, j, k).getModel();

                    if (blockModel.collision)
                    {
                        AABB box = blockModel.collisionBox;

                        boxes.add(new AABB(i + box.x, j + box.y, k + box.z, box.w, box.h, box.d));
                    }
                }
            }
        }

        for (WorldObject object : this.objects)
        {
            object.addCollisionBoxes(boxes);
        }

        return boxes;
    }

    public Entity getEntity(Camera camera)
    {
        return this.getEntity(camera, null);
    }

    public Entity getEntity(Camera camera, Entity exception)
    {
        return this.getEntity(camera.position, camera.getLookDirection(), exception);
    }

    public Entity getEntity(Vector3d origin, Vector3f direction)
    {
        return this.getEntity(origin, direction, null);
    }

    public Entity getEntity(Vector3d origin, Vector3f direction, Entity exception)
    {
        RayTraceResult result = new RayTraceResult();

        RayTracer.traceEntity(result, this, origin, direction, 128, exception);

        return result.type == RayTraceType.ENTITY ? result.entity : null;
    }

    public Vector2f getLighting(double x, double y, double z)
    {
        Vector2f result = new Vector2f();

        int minX = (int) Math.floor(x);
        int minY = (int) Math.floor(y);
        int minZ = (int) Math.floor(z);
        int maxX = minX + 1;
        int maxY = minY + 1;
        int maxZ = minZ + 1;

        double ax = x - minX;
        double ay = y - minY;
        double az = z - minZ;

        int l000 = this.chunks.getLighting(minX, minY, minZ);
        int l100 = this.chunks.getLighting(maxX, minY, minZ);
        int l001 = this.chunks.getLighting(minX, minY, maxZ);
        int l101 = this.chunks.getLighting(maxX, minY, maxZ);
        int l010 = this.chunks.getLighting(minX, maxY, minZ);
        int l110 = this.chunks.getLighting(maxX, maxY, minZ);
        int l011 = this.chunks.getLighting(minX, maxY, maxZ);
        int l111 = this.chunks.getLighting(maxX, maxY, maxZ);

        double top = Interpolations.bilerp(ax, az, l000, l100, l001, l101);
        double bottom = Interpolations.bilerp(ax, az, l010, l110, l011, l111);

        result.x = (float) Interpolations.lerp(top, bottom, ay) / 15F;

        return result;
    }

    public <T extends WorldObject> List<T> getObjects(Class<T> type)
    {
        List<T> objects = new ArrayList<>();

        for (WorldObject object : this.objects)
        {
            if (object.getClass() == type)
            {
                objects.add((T) object);
            }
        }

        return objects;
    }

    public List<WorldObject> getObjects(String id)
    {
        List<WorldObject> worldObjects = new ArrayList<>();

        for (WorldObject object : this.objects)
        {
            if (object.id.equals(id))
            {
                worldObjects.add(object);
            }
        }

        return worldObjects;
    }

    public void saveAll(boolean force)
    {
        for (ChunkCell cell : this.chunks.getCells())
        {
            if (cell != null && (cell.unsaved || force))
            {
                this.save(cell);
            }
        }
    }

    public void save(ChunkCell cell)
    {
        this.storage.save(this, cell);
    }

    public boolean read(ChunkCell chunk)
    {
        return this.storage.read(this, chunk);
    }
}