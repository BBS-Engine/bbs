package mchorse.bbs.ui.world.tools;

import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.voxel.processor.Processor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import org.joml.Vector3i;

import java.util.Set;

public abstract class UIToolProcessorPainter extends UIToolPainting
{
    public UIToolProcessorPainter(UIWorldEditorPanel editor)
    {
        super(editor);
    }

    @Override
    protected void placeBlock(RayTraceResult result, Vector3i center)
    {
        Vector3i min = new Vector3i(center).sub(this.size / 2, this.size / 2, this.size / 2);
        Vector3i max = new Vector3i(min).add(this.size - 1, this.size - 1, this.size - 1);
        Processor processor = this.createProcessor(result);

        processor.process(min, max, this.getProxy());

        Set<Vector3i> placed = processor.getPlaced();

        if (placed != null)
        {
            this.placedBlocks.addAll(placed);
        }
    }

    protected abstract Processor createProcessor(RayTraceResult result);

    @Override
    public void render(RenderingContext context, RayTraceResult result)
    {
        super.render(context, result);

        Vector3i cursor = this.getCursor(result);

        if (this.size > 1)
        {
            int half = this.size / 2;

            Draw.renderBox(context, cursor.x - half, cursor.y - half, cursor.z - half, this.size, this.size, this.size);
        }

        Axis limit = UIToolPainting.getLimit();

        if (limit != null)
        {
            Vector3i min = this.painting ? this.lastBlock : cursor;

            context.stack.push();
            context.stack.translateRelative(context.getCamera(), min.x + 0.5, min.y + 0.5, min.z + 0.5);

            Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);

            CommonShaderAccess.setModelView(shader, context.stack);

            context.stack.pop();

            /* Draw axes */
            VAOBuilder builder = context.getVAO().setup(shader);
            float w = 0.15F;
            float h = 100.05F;

            GLStates.depthMask(false);

            builder.begin();

            if (limit == Axis.X) Draw.fillBox(builder, -h, -w, -w, h, w, w, 0, 0, 0);
            else if (limit == Axis.Y) Draw.fillBox(builder, -w, -h, -w, w, h, w, 0, 0, 0);
            else if (limit == Axis.Z) Draw.fillBox(builder, -w, -w, -h, w, w, h, 0, 0, 0);

            builder.render();

            GLStates.depthMask(true);

            w = 0.1F;
            h = 100;

            builder.begin();

            if (limit == Axis.X) Draw.fillBox(builder, -h, -w, -w, h, w, w, 1, 0, 0);
            else if (limit == Axis.Y) Draw.fillBox(builder, -w, -h, -w, w, h, w, 0, 1, 0);
            else if (limit == Axis.Z) Draw.fillBox(builder, -w, -w, -h, w, w, h, 0, 0, 1);

            builder.render();
        }
    }
}