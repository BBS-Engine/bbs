package mchorse.bbs.ui.world.tools;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.VectorUtils;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.voxel.utils.BlockSelection;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public abstract class UIToolSelectionBase extends UITool
{
    public UIToolSelectionBase(UIWorldEditorPanel editor)
    {
        super(editor);
    }

    protected PlaneSelection getSelection()
    {
        return this.getSelection(Window.isShiftPressed());
    }

    protected PlaneSelection getSelection(boolean furthest)
    {
        BlockSelection selection = this.editor.getSelection();

        if (selection.isEmpty())
        {
            return null;
        }

        List<PlaneSelection> planes = getSelectionBoxPlanes(selection);
        List<PlaneSelection> planeSelections = new ArrayList<PlaneSelection>();
        Camera camera = this.editor.getBridge().get(IBridgeCamera.class).getCamera();
        AABB aabb = AABB.fromTwoPoints(selection.getMin().x, selection.getMin().y, selection.getMin().z, selection.getMax().x, selection.getMax().y, selection.getMax().z);

        /* This needed to solve issues with intersection impression */
        aabb.expand(0.1D, 0.1D, 0.1D);

        for (PlaneSelection plane : planes)
        {
            Vector3d intersection = VectorUtils.intersectPlane(plane.axis, camera.position, camera.getMouseDirection(), plane.block);

            if (intersection != null && aabb.contains(intersection))
            {
                planeSelections.add(new PlaneSelection(plane.axis, plane.side, intersection));
            }
        }

        if (!planeSelections.isEmpty())
        {
            planeSelections.sort((a, b) ->
            {
                PlaneSelection aa = furthest ? b : a;
                PlaneSelection bb = furthest ? a : b;

                return (int) (aa.block.distanceSquared(camera.position) - bb.block.distanceSquared(camera.position));
            });

            PlaneSelection plane = planeSelections.get(0);
            Vector3d first = plane.block;
            Vector3d b = new Vector3d((int) Math.floor(first.x), (int) Math.floor(first.y), (int) Math.floor(first.z));

            return new PlaneSelection(plane.axis, plane.side, b);
        }

        return null;
    }

    protected List<PlaneSelection> getSelectionBoxPlanes(BlockSelection block)
    {
        List<PlaneSelection> planes = new ArrayList<PlaneSelection>();
        Vector3d center = block.getCenter();

        planes.add(new PlaneSelection(Axis.X, -1, new Vector3d(block.getMin().x, center.y, center.z)));
        planes.add(new PlaneSelection(Axis.X,  1, new Vector3d(block.getMax().x, center.y, center.z)));
        planes.add(new PlaneSelection(Axis.Y, -1, new Vector3d(center.x, block.getMin().y, center.z)));
        planes.add(new PlaneSelection(Axis.Y,  1, new Vector3d(center.x, block.getMax().y, center.z)));
        planes.add(new PlaneSelection(Axis.Z, -1, new Vector3d(center.x, center.y, block.getMin().z)));
        planes.add(new PlaneSelection(Axis.Z,  1, new Vector3d(center.x, center.y, block.getMax().z)));

        return planes;
    }

    protected void renderPlaneSelection(RenderingContext context, PlaneSelection plane)
    {
        if (plane == null)
        {
            return;
        }

        /* Render preview of expanding */
        BlockSelection selection = this.editor.getSelection();
        Vector3i min = selection.getMin();
        Vector3i size = selection.getSize();
        Color color = new Color();

        if (plane.axis == Axis.X) color.r = 1;
        if (plane.axis == Axis.Y) color.g = 1;
        if (plane.axis == Axis.Z) color.b = 1;

        context.stack.push();
        context.stack.translateRelative(context.getCamera(), min.x, min.y, min.z);

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);

        CommonShaderAccess.setModelView(shader, context.stack);

        context.stack.pop();

        CommonShaderAccess.setColor(shader, 1, 1, 1, 0.85F);

        VAOBuilder builder = context.getVAO().setup(shader);

        GLStates.depthTest(false);

        builder.begin();

        if (plane.axis == Axis.X)
        {
            int axis = plane.side < 0 ? 0 : size.x;

            Draw.fillBox(builder, axis, 0, 0, axis, size.y, size.z, color.r, color.g, color.b);
        }
        else if (plane.axis == Axis.Y)
        {
            int axis = plane.side < 0 ? 0 : size.y;

            Draw.fillBox(builder, 0, axis, 0, size.x, axis, size.z, color.r, color.g, color.b);
        }
        else if (plane.axis == Axis.Z)
        {
            int axis = plane.side < 0 ? 0 : size.z;

            Draw.fillBox(builder, 0, 0, axis, size.x, size.y, axis, color.r, color.g, color.b);
        }

        builder.render();
        CommonShaderAccess.resetColor(shader);

        GLStates.depthTest(true);
    }

    public static class PlaneSelection
    {
        public Axis axis;
        public int side;
        public Vector3d block;

        public PlaneSelection(Axis axis, int side, Vector3d block)
        {
            this.axis = axis;
            this.side = side;
            this.block = block;
        }
    }
}