package mchorse.bbs.ui.world.tools.schematic;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.UIModelRenderer;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.function.Consumer;

public class UISchematicRenderer extends UIModelRenderer
{
    private ChunkDisplay display;
    private Consumer<Vector3i> callback;
    private RayTraceResult result = new RayTraceResult();

    public UISchematicRenderer(ChunkDisplay display, Consumer<Vector3i> callback)
    {
        this.display = display;
        this.callback = callback;

        this.setPosition(display.chunk.w / 2F, display.chunk.h / 2F, display.chunk.d / 2F);
        this.setDistance(display.chunk.d);

        this.grid = false;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (context.mouseButton == 1 && this.result.type == RayTraceType.BLOCK)
        {
            if (this.callback != null)
            {
                this.callback.accept(this.result.block);
            }

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected void renderUserModel(UIContext context)
    {
        ChunkBuilder chunkBuilder = this.getContext().menu.bridge.get(IBridgeWorld.class).getChunkBuilder();
        Shader shader = context.render.getShaders().get(chunkBuilder.getAttributes());
        MatrixStack stack = context.render.getStack();

        CommonShaderAccess.setModelView(shader, stack);
        shader.bind();
        context.render.getTextures().bind(chunkBuilder.models.atlas);
        this.display.render();

        Vector3f mouseDirection = this.camera.getMouseDirection(context.mouseX, context.mouseY, this.area);

        RayTracer.trace(this.result, this.display.chunk, this.camera.position, mouseDirection, 64);

        if (!this.result.type.isMissed())
        {
            Draw.renderBlockAABB(context.render, this.display.chunk, this.result.block.x, this.result.block.y, this.result.block.z);
        }
    }
}