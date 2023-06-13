package mchorse.bbs.world.entities;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.IWorldObject;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.components.BasicComponent;
import mchorse.bbs.world.entities.components.Component;
import mchorse.bbs.world.entities.components.IRenderableComponent;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Entity implements IMapSerializable, IWorldObject
{
    /**
     * Static vector for calculating rendering information 
     */
    public static Vector3d vector = new Vector3d();

    /**
     * The type ID of this entity
     */
    public final Link id;

    /**
     * Reference to the current world in which this entity is located 
     */
    public World world;

    /**
     * Entity's UUID
     */
    private UUID uuid = UUID.randomUUID();

    /* Components */
    private final List<Component> components;
    private final Map<Class, Component> named;
    private final List<IRenderableComponent> renderables;
    public final BasicComponent basic;

    /* Entity states */
    private boolean remove;
    public boolean canBeSaved = true;

    /* Rendering */
    private Matrix4f transform = new Matrix4f();

    public Entity(Link id, List<EntityRecord> records)
    {
        this.id = id;

        List<Component> components = new ArrayList<Component>();
        HashMap<Class, Component> named = new HashMap<Class, Component>();
        List<IRenderableComponent> renderables = new ArrayList<IRenderableComponent>();

        for (EntityRecord record : records)
        {
            record.component.setEntity(this);

            components.add(record.component);
            named.put(record.key, record.component);

            if (record.component instanceof IRenderableComponent)
            {
                renderables.add((IRenderableComponent) record.component);
            }
        }

        renderables.sort(Comparator.comparingInt(IRenderableComponent::getRenderPriority));

        this.components = Collections.unmodifiableList(components);
        this.named = Collections.unmodifiableMap(named);
        this.basic = this.get(BasicComponent.class);
        this.renderables = Collections.unmodifiableList(renderables);
    }

    public List<Component> getAll()
    {
        return Collections.unmodifiableList(this.components);
    }

    public <T> T get(Class<T> component)
    {
        Component result = this.named.get(component);

        if (result == null)
        {
            return null;
        }

        return (T) result;
    }

    public <T> boolean has(Class<T> component)
    {
        return this.named.containsKey(component);
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public void setPosition(double x, double y, double z)
    {
        this.basic.setPosition(x, y, z);
        this.basic.prevPosition.set(this.basic.position);
    }

    public void setRotation(float pitch, float yaw)
    {
        this.basic.setRotation(pitch, yaw);
        this.basic.prevRotation.set(this.basic.rotation);
    }

    public boolean isRemoved()
    {
        return this.remove;
    }

    public void remove()
    {
        this.remove = true;

        for (Component component : this.components)
        {
            component.entityWasRemoved();
        }
    }

    public void update()
    {
        for (Component component : this.components)
        {
            component.preUpdate();
        }

        for (Component component : this.components)
        {
            component.postUpdate();
        }
    }

    @Override
    public AABB getPickingHitbox()
    {
        return this.basic.hitbox;
    }

    /* Serialization / Deserialization */

    @Override
    public MapType toData()
    {
        MapType map = new MapType();

        map.putString("id", this.id.toString());
        map.putString("uuid", this.uuid.toString());
        map.putBool("canBeSaved", this.canBeSaved);
        this.toData(map);

        return map;
    }

    @Override
    public void toData(MapType data)
    {
        MapType components = new MapType();

        for (Component component : this.components)
        {
            components.put(component.getId().toString(), component.toData());
        }

        data.put("components", components);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("uuid"))
        {
            this.uuid = UUID.fromString(data.getString("uuid"));
        }

        if (data.has("canBeSaved"))
        {
            this.canBeSaved = data.getBool("canBeSaved");
        }

        MapType components = data.getMap("components", null);

        if (components == null)
        {
            return;
        }

        for (Component component : this.components)
        {
            MapType element = components.getMap(component.getId().toString());

            if (element != null)
            {
                component.fromData(element);
            }
        }
    }

    /* Render related methods */

    /**
     * Calculates the position vector for rendering
     */
    public void calculatePositionForRender(Vector3d vec, float transition)
    {
        BasicComponent component = this.basic;

        component.prevPosition.lerp(component.position, transition, vec);
    }

    public Matrix4f getMatrixForRender(Camera camera, float transition)
    {
        this.calculatePositionForRender(vector, transition);

        return this.transform.identity().translate(camera.getRelative(vector));
    }

    public Matrix4f getMatrixForRenderWithRotation(Camera camera, float transition)
    {
        BasicComponent component = this.basic;
        float yaw = (float) Math.PI - Interpolations.lerp(component.prevRotation.z, component.rotation.z, transition);

        return this.getMatrixForRender(camera, transition).rotateY(yaw);
    }

    public void render(RenderingContext context)
    {
        for (IRenderableComponent component : this.renderables)
        {
            component.render(context);
        }

        if (context.isDebug())
        {
            Vector3d vec = Vectors.TEMP_3D.set(0, 0, 0);
            BasicComponent basic = this.basic;

            this.calculatePositionForRender(vec, context.getTransition());

            /* Draw look vector */
            Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);
            VAOBuilder builder = context.getVAO().setup(shader);
            final float thickness = 0.01F;

            builder.begin();

            Draw.fillBox(builder, -thickness, -thickness, -thickness, thickness, thickness, 2F, 1F, 0F, 0F);

            context.stack.push();
            context.stack.translateRelative(context.getCamera(), vec.x, vec.y + basic.getEyeHeight(), vec.z);
            context.stack.rotateY(-basic.rotation.y);
            context.stack.rotateX(-basic.rotation.x + MathUtils.PI);

            CommonShaderAccess.setModelView(shader, context.stack);
            builder.render();

            context.stack.pop();

            /* Draw hitbox */
            vec.x -= basic.hitbox.w / 2;
            vec.z -= basic.hitbox.d / 2;

            Draw.renderBox(context, vec.x, vec.y, vec.z, basic.hitbox.w, basic.hitbox.h, basic.hitbox.d);
        }
    }
}