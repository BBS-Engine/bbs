package mchorse.bbs.ui.world;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

public abstract class UICommonWorldEditor <T> extends UIWorldPanel
{
    public static final int LIST_HEIGHT = 20 + UIStringList.DEFAULT_HEIGHT * 6;

    public UISearchList<T> objects;
    public UIScrollView editor;

    protected T object;
    protected T hoveredObject;

    public UICommonWorldEditor(UIDashboard dashboard)
    {
        super(dashboard);

        this.objects = this.createSearchList((l) -> this.fill(l.get(0), false));
        this.objects.list.cancelScrollEdge();
        this.objects.label(UIKeys.GENERAL_SEARCH);
        this.objects.context((m) ->
        {
            MapType type = Window.getClipboardMap("_Copy" + this.getClass().getSimpleName());

            m.action(Icons.ADD, UIKeys.WORLD_CONTEXT_ADD, this::addObject);

            if (this.object != null) m.action(Icons.COPY, UIKeys.WORLD_CONTEXT_COPY, this::copyObject);

            if (type != null)
            {
                m.action(Icons.PASTE, UIKeys.WORLD_CONTEXT_PASTE, () -> this.pasteObject(type));
            }

            if (this.object != null) m.action(Icons.REMOVE, UIKeys.WORLD_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeObject);
        });
        this.objects.list.background();
        this.objects.relative(this).xy(10, 10).w(160).h(LIST_HEIGHT);

        this.editor = UI.scrollView(5, 10);
        this.editor.scroll.cancelScrolling().opposite();
        this.editor.relative(this).y(10 + LIST_HEIGHT).w(180).h(1F, -10 - LIST_HEIGHT);

        this.add(this.objects, this.editor);

        this.keys().register(Keys.WORLD_MOVE_CENTER, this::moveToCenter).active(() -> this.object != null);
        this.keys().register(Keys.WORLD_MOVE_TO_CURSOR, this::moveToCursor).active(() -> this.object != null);
    }

    private void moveToCenter()
    {
        AABB hitbox = this.getHitbox(this.object);

        this.dashboard.orbit.position.set(hitbox.x + hitbox.w / 2, hitbox.y + hitbox.h / 2, hitbox.z + hitbox.d / 2);
    }

    private void moveToCursor()
    {
        RayTraceResult result = new RayTraceResult();
        Camera camera = this.dashboard.bridge.get(IBridgeCamera.class).getCamera();
        MouseInput input = BBS.getEngine().mouse;

        RayTracer.trace(result, this.dashboard.bridge.get(IBridgeWorld.class).getWorld().chunks, camera.position, camera.getMouseDirection(input.x, input.y), 64);

        if (result.type == RayTraceType.BLOCK)
        {
            this.moveToCursor(this.object, result.hit);
        }
    }

    protected abstract void moveToCursor(T object, Vector3d hit);

    protected abstract UISearchList<T> createSearchList(Consumer<List<T>> callback);

    protected abstract void addObject();

    protected abstract void addObject(T object);

    protected void copyObject()
    {
        Window.setClipboard(this.toData(this.object), "_Copy" + this.getClass().getSimpleName());
    }

    protected abstract MapType toData(T object);

    protected abstract void pasteObject(MapType type);

    protected abstract void removeObject();

    protected void fill(T object, boolean select)
    {
        this.object = object;

        this.editor.removeAll();
        this.editor.setVisible(object != null);

        if (object != null)
        {
            this.setupEditor(object);
        }

        this.editor.resize();

        if (select)
        {
            this.objects.list.setCurrent(object);
        }
    }

    protected abstract void setupEditor(T object);

    @Override
    public void appear()
    {
        List<T> objects = this.getList();

        this.objects.list.setList(objects);

        if (this.object == null || !objects.contains(this.object))
        {
            this.fill(objects.isEmpty() ? null : objects.get(0), true);
        }
        else
        {
            this.fill(this.object, true);
        }
    }

    protected abstract List<T> getList();

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.hoveredObject != null && context.mouseButton == 1)
        {
            this.fill(this.hoveredObject, true);

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void renderInWorld(RenderingContext context)
    {
        if (!this.canBeSeen())
        {
            return;
        }

        MouseInput input = BBS.getEngine().mouse;
        Camera camera = context.getCamera();

        this.hoveredObject = this.getClosestObject(camera.position, camera.getMouseDirection(input.x, input.y));

        for (T object : this.objects.list.getList())
        {
            this.renderObject(context, object);
        }
    }

    protected void renderObject(RenderingContext context, T object)
    {
        AABB aabb = this.getHitbox(object);

        if (object == this.hoveredObject || object == this.objects.list.getCurrentFirst())
        {
            Draw.renderBox(context, aabb.x, aabb.y, aabb.z, aabb.w, aabb.h, aabb.d, 0, 0.5F, 1F);
        }
        else
        {
            Draw.renderBox(context, aabb.x, aabb.y, aabb.z, aabb.w, aabb.h, aabb.d);
        }
    }

    /**
     * Get the closest object to the camera
     */
    private T getClosestObject(Vector3d finalPosition, Vector3f mouseDirection)
    {
        T closest = null;

        for (T object : this.objects.list.getList())
        {
            AABB aabb = this.getHitbox(object);

            if (aabb.intersectsRay(finalPosition, mouseDirection))
            {
                if (closest == null)
                {
                    closest = object;
                }
                else
                {
                    AABB aabb2 = this.getHitbox(closest);

                    if (finalPosition.distanceSquared(aabb.x, aabb.y, aabb.z) < finalPosition.distanceSquared(aabb2.x, aabb2.y, aabb2.z))
                    {
                        closest = object;
                    }
                }
            }
        }
        return closest;
    }

    protected abstract AABB getHitbox(T object);
}