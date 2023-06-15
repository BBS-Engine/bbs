package mchorse.bbs.forms.renderers;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.forms.forms.BlockForm;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.BlockLink;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.world.entities.Entity;

public class BlockFormRenderer extends FormRenderer<BlockForm>
{
    public BlockFormRenderer(BlockForm form)
    {
        super(form);
    }

    private IBlockVariant getVariant(ChunkBuilder builder)
    {
        BlockLink link = this.form.block.get();

        if (link != null)
        {
            return builder.models.getVariant(link);
        }

        return builder.models.variants.isEmpty() ? null : builder.models.variants.get(0);
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        ChunkBuilder chunkBuilder = context.menu.bridge.get(IBridgeWorld.class).getChunkBuilder();
        IBlockVariant variant = this.getVariant(chunkBuilder);

        if (variant != null)
        {
            int w = x2 - x1;
            int h = y2 - y1;
            int scale = Math.min(w, h) / 2;

            chunkBuilder.renderInUI(context, variant, x1 + w / 2, y1 + h / 2, scale);
        }
    }

    @Override
    protected void render3D(Entity entity, RenderingContext context)
    {
        ChunkBuilder chunkBuilder = context.getWorld().bridge.get(IBridgeWorld.class).getChunkBuilder();
        IBlockVariant variant = this.getVariant(chunkBuilder);

        if (variant != null)
        {
            Shader shader = context.getShaders().get(chunkBuilder.getAttributes());
            VAOBuilder builder = context.getVAO().setup(shader, VAO.INDICES);

            context.getTextures().bind(chunkBuilder.models.atlas);

            CommonShaderAccess.setModelView(shader, context.stack);

            builder.begin(-0.5F, 0, -0.5F);
            chunkBuilder.resetIndex().buildBlock(variant, 0, 0, 0, builder);
            builder.render();
        }
    }
}