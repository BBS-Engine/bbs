package mchorse.bbs.ui.world.tools;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.VectorUtils;
import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.processor.CopyProcessor;
import mchorse.bbs.voxel.processor.PasteProcessor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.voxel.utils.BlockSelection;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class UIToolExtrude extends UIToolSelectionBase
{
    private PlaneSelection plane;
    private ChunkDisplay display;
    private int times;

    public UIToolExtrude(UIWorldEditorPanel editor)
    {
        super(editor);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.UPLOAD, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_EXTRUDE, Direction.RIGHT);

        return icon;
    }

    @Override
    public void begin(RayTraceResult result, int mouseButton)
    {
        super.begin(result, mouseButton);

        PlaneSelection plane = this.getSelection();
        BlockSelection selection = this.editor.getSelection();

        if (selection.isEmpty() || plane == null)
        {
            return;
        }

        Vector3i size = selection.getSize();
        Chunk chunk = new Chunk(size.x, size.y, size.z, this.getProxy().getAir());
        CopyProcessor copy = new CopyProcessor(chunk);

        copy.process(selection.getMin(), selection.getMax(), this.getProxy());

        this.plane = plane;
        this.display = new ChunkDisplay(null, chunk, 0, 0, 0);
        this.times = 1;
    }

    @Override
    public void drag(RayTraceResult result)
    {
        super.drag(result);

        if (this.plane == null)
        {
            return;
        }

        BlockSelection selection = this.editor.getSelection();
        Camera camera = this.editor.getBridge().get(IBridgeCamera.class).getCamera();
        Vector3d center = selection.getCenter();
        Vector3d intersection = VectorUtils.intersectPlanePerpendicular(this.plane.axis, camera.position, camera.getMouseDirection(), center);

        if (intersection != null)
        {
            double diff = 0;
            int side = 0;

            if (this.plane.axis == Axis.X)
            {
                diff = intersection.x - center.x;
                side = this.display.chunk.w;
            }
            else if (this.plane.axis == Axis.Y)
            {
                diff = intersection.y - center.y;
                side = this.display.chunk.h;
            }
            else if (this.plane.axis == Axis.Z)
            {
                diff = intersection.z - center.z;
                side = this.display.chunk.d;
            }

            this.times = (int) Math.max(Math.round(Math.abs(diff) / side), 1);
        }
    }

    @Override
    public void end(RayTraceResult result)
    {
        if (this.plane != null && this.display != null)
        {
            this.display.delete();

            PlaneSelection plane = this.plane;
            BlockSelection selection = this.editor.getSelection();
            Chunk chunk = this.display.chunk;

            for (int i = 0; i < this.times; i++)
            {
                if (plane.axis == Axis.X) selection.move(plane.side * chunk.w, 0, 0);
                else if (plane.axis == Axis.Y) selection.move(0, plane.side * chunk.h, 0);
                else if (plane.axis == Axis.Z) selection.move(0, 0, plane.side * chunk.d);

                PasteProcessor paste = new PasteProcessor(chunk);

                paste.process(selection.getMin(), selection.getMax(), this.getProxy());
            }

            this.editor.selected = true;
            this.editor.updateSelection();
        }

        super.end(result);

        this.plane = null;
        this.display = null;
    }

    @Override
    public void render(RenderingContext context, RayTraceResult result)
    {
        super.render(context, result);

        if (this.plane != null)
        {
            if (this.display.display == null)
            {
                context.getWorld().chunks.builder.build(context, this.display, null);
            }

            Shader shader = context.getShaders().get(context.getWorld().chunks.builder.getAttributes());
            MatrixStack stack = context.stack;

            context.getTextures().bind(context.getWorld().chunks.builder.models.atlas);

            CommonShaderAccess.setColor(shader, 0, 0.75F, 1F, 1F);

            Vector3i vector = new Vector3i(this.editor.getSelection().getMin());
            Vector3i direction = new Vector3i();

            if (this.plane.axis == Axis.X) direction.x = this.plane.side * this.display.chunk.w;
            else if (this.plane.axis == Axis.Y) direction.y = this.plane.side * this.display.chunk.h;
            else if (this.plane.axis == Axis.Z) direction.z = this.plane.side * this.display.chunk.d;

            shader.bind();
            vector.add(direction);

            for (int i = 0; i < this.times; i++)
            {
                stack.push();
                stack.translateRelative(context.getCamera(), vector.x, vector.y, vector.z);
                CommonShaderAccess.setModelView(shader, stack);
                stack.pop();

                this.display.render();
                vector.add(direction);
            }

            CommonShaderAccess.resetColor(shader);
        }

        this.renderPlaneSelection(context, this.plane == null ? this.getSelection() : this.plane);
    }
}