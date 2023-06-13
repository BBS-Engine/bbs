package mchorse.bbs.ui.world.entities;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UICommonWorldEditor;
import mchorse.bbs.ui.world.entities.components.UIEntityComponent;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.Component;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class UIEntitiesPanel extends UICommonWorldEditor<Entity>
{
    public static void setupEntityEditor(UIScrollView editor, Entity object)
    {
        for (Component component : object.getAll())
        {
            try
            {
                Class<? extends UIEntityComponent> clazz = BBS.getFactoryEntityComponents().getData(component);

                if (clazz == null)
                {
                    continue;
                }

                UIEntityComponent ui = clazz.getConstructor(component.getClass()).newInstance(component);

                ui.marginTop(editor.getChildren().isEmpty() ? 0 : 8);
                editor.add(ui);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public UIEntitiesPanel(UIDashboard dashboard)
    {
        super(dashboard);
    }

    @Override
    public boolean canPause()
    {
        return true;
    }

    @Override
    public boolean canRefresh()
    {
        return true;
    }

    @Override
    protected void moveToCursor(Entity object, Vector3d hit)
    {
        object.setPosition(hit.x, hit.y, hit.z);

        this.fill(object, false);
    }

    @Override
    protected UISearchList<Entity> createSearchList(Consumer<List<Entity>> callback)
    {
        return new UISearchList<Entity>(new UIEntityList(callback));
    }

    @Override
    protected void addObject()
    {
        this.getContext().replaceContextMenu((menu) ->
        {
            World world = this.dashboard.bridge.get(IBridgeWorld.class).getWorld();

            for (Link key : world.architect.getKeys())
            {
                menu.shadow().action(Icons.ADD, UIKeys.ENTITIES_CONTEXT_ADD.format(UIKeys.C_ENTITIES.get(key)), () ->
                {
                    this.addObject(world.architect.create(key));
                });
            }
        });
    }

    @Override
    protected void addObject(Entity entity)
    {
        RayTraceResult result = new RayTraceResult();
        UIContext context = this.getContext();
        Camera camera = context.menu.bridge.get(IBridgeCamera.class).getCamera();

        RayTracer.trace(result, context.menu.bridge.get(IBridgeWorld.class).getWorld().chunks, camera.position, camera.getLookDirection(), 64);

        if (result.type == RayTraceType.BLOCK)
        {
            entity.setPosition(result.hit.x, result.hit.y, result.hit.z);
        }
        else
        {
            Vector3f look = camera.getLookDirection().mul(3);
            Vector3d position = new Vector3d(camera.position).add(look.x, look.y, look.z);

            entity.setPosition(position.x, position.y, position.z);
        }

        this.dashboard.bridge.get(IBridgeWorld.class).getWorld().addEntity(entity);
        this.fill(entity, true);
        this.objects.list.update();
    }

    @Override
    protected MapType toData(Entity object)
    {
        return object.toData();
    }

    @Override
    protected void pasteObject(MapType type)
    {
        this.addObject(this.dashboard.bridge.get(IBridgeWorld.class).getWorld().architect.create(type));
    }

    @Override
    protected void removeObject()
    {
        List<Entity> list = this.objects.list.getList();
        int index = list.indexOf(this.object);

        if (index == -1)
        {
            this.fill(null, true);
        }
        else
        {
            this.dashboard.bridge.get(IBridgeWorld.class).getWorld().removeEntity(this.object);
            this.fill(list.isEmpty() ? null : list.get(MathUtils.clamp(index, 0, list.size() - 1)), true);
            this.objects.list.update();
        }
    }

    @Override
    protected void setupEditor(Entity object)
    {
        setupEntityEditor(this.editor, object);
    }

    @Override
    protected List<Entity> getList()
    {
        return this.dashboard.bridge.get(IBridgeWorld.class).getWorld().entities;
    }

    @Override
    protected AABB getHitbox(Entity object)
    {
        return object.basic.hitbox;
    }
}