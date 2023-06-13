package mchorse.bbs.ui.world.tools;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.VectorUtils;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.utils.BlockSelection;
import org.joml.Vector3d;

public class UIToolSelection extends UIToolSelectionBase
{
    public UIToolSelectionBase.PlaneSelection selection;
    public Vector3d lastMin;
    public Vector3d lastMax;
    public Vector3d lastIntersection;

    public UIToolSelection(UIWorldEditorPanel editor)
    {
        super(editor);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.FULLSCREEN, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_SELECTION, Direction.RIGHT).marginTop(10);

        return icon;
    }

    @Override
    public void begin(RayTraceResult result, int mouseButton)
    {
        super.begin(result, mouseButton);

        BlockSelection selection = this.editor.getSelection();

        this.lastMin = new Vector3d();
        this.lastMax = new Vector3d();
        this.selection = this.getSelection();

        VectorUtils.min(selection.getA(), selection.getB(), this.lastMin);
        VectorUtils.max(selection.getA(), selection.getB(), this.lastMax);

        this.select(result);
    }

    @Override
    public void end(RayTraceResult result)
    {
        super.end(result);

        this.lastMin = null;
        this.lastMax = null;
        this.lastIntersection = null;
        this.selection = null;
    }

    @Override
    public void drag(RayTraceResult result)
    {
        super.drag(result);

        this.select(result);
    }

    private void select(RayTraceResult result)
    {
        if (this.selection == null)
        {
            return;
        }

        BlockSelection selection = this.editor.getSelection();
        Camera camera = this.editor.getBridge().get(IBridgeCamera.class).getCamera();
        Vector3d intersection = VectorUtils.intersectPlanePerpendicular(this.selection.axis, camera.position, camera.getMouseDirection(), this.selection.block);

        if (intersection != null && this.lastIntersection != null)
        {
            Vector3d vector = this.selection.side > 0 ? this.lastMax : this.lastMin;
            Vector3d other = this.selection.side > 0 ? this.lastMin : this.lastMax;
            double value = 0;

            if (this.selection.axis == Axis.X) value = Math.round(intersection.x - this.lastIntersection.x);
            else if (this.selection.axis == Axis.Y) value = Math.round(intersection.y - this.lastIntersection.y);
            else if (this.selection.axis == Axis.Z) value = Math.round(intersection.z - this.lastIntersection.z);

            if (value != 0)
            {
                if (this.lastMouseButton == 0)
                {
                    if (this.selection.axis == Axis.X) vector.x += value;
                    else if (this.selection.axis == Axis.Y) vector.y += value;
                    else if (this.selection.axis == Axis.Z) vector.z += value;

                    selection.set(vector, other);
                }
                else if (this.lastMouseButton == 1)
                {
                    if (this.selection.axis == Axis.X) selection.move((int) value, 0, 0);
                    else if (this.selection.axis == Axis.Y) selection.move(0, (int) value, 0);
                    else if (this.selection.axis == Axis.Z) selection.move(0, 0, (int) value);
                }

                this.lastIntersection = intersection;
            }
        }

        if (this.lastIntersection == null)
        {
            this.lastIntersection = intersection;
        }

        this.editor.selected = true;
        this.editor.updateSelection();
    }

    @Override
    public void render(RenderingContext context, RayTraceResult result)
    {
        super.render(context, result);

        this.renderPlaneSelection(context, this.selection == null ? this.getSelection() : this.selection);
    }
}