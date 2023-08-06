package mchorse.bbs.ui.world.tools;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.VectorUtils;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;

public abstract class UIToolPainting extends UITool
{
    protected int size = 1;
    protected int delay = 3;

    protected UITrackpad sizeElement;
    protected UITrackpad delayElement;
    protected UIToggle continuous;

    protected Axis limit;
    protected Set<Vector3i> placedBlocks = new HashSet<>();
    protected int placeDelay;
    protected boolean painting;

    protected Vector3i lastBlock = new Vector3i();

    public static Axis getLimit()
    {
        if (Window.isShiftPressed())
        {
            return Axis.Y;
        }
        else if (Window.isCtrlPressed())
        {
            return Axis.X;
        }
        else if (Window.isAltPressed())
        {
            return Axis.Z;
        }

        return null;
    }

    public UIToolPainting(UIWorldEditorPanel editor)
    {
        super(editor);

        this.sizeElement = new UITrackpad((v) -> this.size = v.intValue()).limit(1).integer();
        this.sizeElement.setValue(this.size);

        this.delayElement = new UITrackpad((v) -> this.delay = v.intValue()).limit(1).integer();
        this.delayElement.setValue(this.delay);

        this.continuous = new UIToggle(UIKeys.WORLD_EDITOR_TOOLS_PAINTING_CONTINUOUS, null);
        this.continuous.setValue(true);

        this.panel.add(UI.label(UIKeys.WORLD_EDITOR_TOOLS_PAINTING_SIZE), this.sizeElement);
        this.panel.add(UI.label(UIKeys.WORLD_EDITOR_TOOLS_PAINTING_DELAY), this.delayElement);
        this.panel.add(this.continuous);
    }

    @Override
    public void begin(RayTraceResult result, int mouseButton)
    {
        super.begin(result, mouseButton);

        this.limit = getLimit();

        Vector3i min = this.getCursor(result);

        this.painting = true;
        this.firstBlock.set(min);
        this.lastBlock.set(min);
        this.placeBlock(result, min);
        this.placeDelay = this.delay;
    }

    protected Vector3i getCursor(RayTraceResult result)
    {
        Vector3i cursor = new Vector3i(result.block);

        if (this.lastMouseButton == 1 && !this.editor.getProxy().isMaskEnabled())
        {
            cursor.add(result.normal);
        }

        return cursor;
    }

    @Override
    public void drag(RayTraceResult result)
    {
        super.drag(result);

        if (this.limit == null)
        {
            if (this.placeDelay >= 0)
            {
                this.placeDelay -= 1;

                if (this.placeDelay == 0)
                {
                    this.placeDelay = this.delay;
                }
                else
                {
                    return;
                }
            }
        }

        Vector3i min = !result.type.isMissed() ? this.getCursor(result) : null;

        if (this.limit != null)
        {
            Camera camera = this.editor.getBridge().get(IBridgeCamera.class).getCamera();
            Vector3d anchor = new Vector3d(this.firstBlock.x + 0.5D, this.firstBlock.y + 0.5D, this.firstBlock.z + 0.5D);
            Vector3d vec = VectorUtils.intersectPlanePerpendicular(this.limit, camera.position, camera.getMouseDirection(), anchor);

            if (vec != null)
            {
                min = new Vector3i((int) Math.floor(vec.x), (int) Math.floor(vec.y), (int) Math.floor(vec.z));

                if (this.limit != Axis.X) min.x = this.firstBlock.x;
                if (this.limit != Axis.Y) min.y = this.firstBlock.y;
                if (this.limit != Axis.Z) min.z = this.firstBlock.z;
            }
        }

        if (min != null)
        {
            double distance = this.lastBlock.distanceSquared(min);

            if ((distance > 1 && this.continuous.getValue()) || this.limit != null)
            {
                for (Vector3i block : UIToolLine.calculate(this.lastBlock, min))
                {
                    this.placeBlock(result, block);
                }
            }
            else
            {
                this.placeBlock(result, min);
            }

            this.lastBlock.set(min);
        }
    }

    protected abstract void placeBlock(RayTraceResult result, Vector3i center);

    @Override
    public void end(RayTraceResult result)
    {
        super.end(result);

        this.limit = null;
        this.placedBlocks.clear();
        this.painting = false;
    }

    @Override
    public boolean mouseScrolled(int scroll)
    {
        if (Window.isShiftPressed())
        {
            this.size = MathUtils.clamp(this.size + (int) Math.copySign(1, scroll), 1, 100);
            this.sizeElement.setValue(this.size);

            return true;
        }

        return false;
    }

    @Override
    public boolean handleRayTracer(RayTraceResult result)
    {
        return this.limit != null || !this.placedBlocks.contains(result.block);
    }
}