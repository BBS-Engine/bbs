package mchorse.bbs.ui.world.objects;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.IFlightSupported;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UICommonWorldEditor;
import mchorse.bbs.ui.world.objects.objects.UIWorldObject;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.objects.WorldObject;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public class UIWorldObjectsPanel extends UICommonWorldEditor<WorldObject> implements IFlightSupported
{
    public UIWorldObjectsPanel(UIDashboard dashboard)
    {
        super(dashboard);
    }

    @Override
    protected void moveToCursor(WorldObject object, Vector3d hit)
    {
        object.position.set(hit);

        this.fill(object, false);
    }

    @Override
    protected UISearchList<WorldObject> createSearchList(Consumer<List<WorldObject>> callback)
    {
        return new UISearchList<WorldObject>(new UIWorldObjectList(callback));
    }

    @Override
    protected void addObject()
    {
        this.getContext().replaceContextMenu((menu) ->
        {
            for (Link key : BBS.getFactoryWorldObjects().getKeys())
            {
                menu.shadow().action(Icons.ADD, UIKeys.WORLD_OBJECTS_CONTEXT_ADD.format(UIKeys.C_WORLD_OBJECT.get(key)), () ->
                {
                    this.addObject(BBS.getFactoryWorldObjects().create(key));
                });
            }
        });
    }

    @Override
    protected void addObject(WorldObject object)
    {
        RayTraceResult result = new RayTraceResult();
        UIContext context = this.getContext();
        Camera camera = context.menu.bridge.get(IBridgeCamera.class).getCamera();

        RayTracer.trace(result, context.menu.bridge.get(IBridgeWorld.class).getWorld().chunks, camera.position, camera.getLookDirection(), 64);

        if (result.type == RayTraceType.BLOCK)
        {
            object.position.set(result.hit);
        }
        else
        {
            Vector3f look = camera.getLookDirection().mul(3);

            object.position.set(camera.position).add(look.x, look.y, look.z);
        }

        this.objects.list.add(object);
        this.fill(object, true);
    }

    @Override
    protected MapType toData(WorldObject object)
    {
        return BBS.getFactoryWorldObjects().toData(object);
    }

    @Override
    protected void pasteObject(MapType type)
    {
        this.addObject(BBS.getFactoryWorldObjects().fromData(type));
    }

    @Override
    protected void removeObject()
    {
        List<WorldObject> list = this.objects.list.getList();
        int index = list.indexOf(this.object);

        if (index == -1)
        {
            this.fill(null, true);
        }
        else
        {
            this.objects.list.remove(this.object);
            this.fill(list.isEmpty() ? null : list.get(MathUtils.clamp(index, 0, list.size() - 1)), true);
        }
    }

    @Override
    protected void setupEditor(WorldObject object)
    {
        try
        {
            UIWorldObject editor = BBS.getFactoryWorldObjects().getData(object).getConstructor().newInstance();

            editor.fillData(object);

            this.editor.add(editor);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected List<WorldObject> getList()
    {
        return this.dashboard.bridge.get(IBridgeWorld.class).getWorld().objects;
    }

    @Override
    protected AABB getHitbox(WorldObject object)
    {
        return object.getPickingHitbox();
    }

    @Override
    protected void renderObject(RenderingContext context, WorldObject object)
    {
        super.renderObject(context, object);

        if (!context.isDebug())
        {
            object.renderDebug(context);
        }
    }
}