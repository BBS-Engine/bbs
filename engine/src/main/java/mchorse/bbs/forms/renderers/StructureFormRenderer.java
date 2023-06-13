package mchorse.bbs.forms.renderers;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.forms.forms.StructureForm;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.world.entities.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class StructureFormRenderer extends FormRenderer<StructureForm>
{
    public StructureFormRenderer(StructureForm form)
    {
        super(form);
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        ChunkBuilder chunkBuilder = context.menu.bridge.get(IBridgeWorld.class).getChunkBuilder();
        ChunkDisplay display = BBS.getStructures().getCachedChunk(this.form.structure.get(), context.render, chunkBuilder);
        int x = (x2 + x1) / 2;
        int y = (y2 + y1) / 2;

        if (display != null)
        {
            Shader shader = context.render.getShaders().get(chunkBuilder.getAttributes());

            Matrix4f model = new Matrix4f();
            Matrix3f normal = new Matrix3f();

            float scale = Math.min(x2 - x1, y2 - y1);

            scale /= Math.max(display.chunk.w, Math.max(display.chunk.h, display.chunk.d)) * 1.25F;

            model.scale(scale, -scale, scale);
            model.rotateX(MathUtils.PI / 5).rotateY(MathUtils.PI / 4);
            model.setTranslation(x, y, 20);
            normal.rotateX(MathUtils.PI / 5);
            model.mul(new Matrix4f().translate(-display.chunk.w / 2F, -display.chunk.h / 2F, -display.chunk.d / 2F));

            shader.bind();
            CommonShaderAccess.setModelView(shader, model, normal);

            Texture texture = context.render.getTextures().getTexture(chunkBuilder.models.atlas);

            GLStates.setupDepthFunction3D();

            texture.bind();

            int filter = texture.getFilter();

            texture.setFilter(GL11.GL_NEAREST);
            display.render();
            texture.setFilter(filter);

            GLStates.setupDepthFunction2D();
        }
        else
        {
            String label = "N/A";

            x -= context.font.getWidth(label) / 2;
            y -= context.font.getHeight() / 2;

            context.font.renderWithShadow(context.render, label, x, y);
        }
    }

    @Override
    protected void render3D(Entity entity, RenderingContext context)
    {
        ChunkBuilder chunkBuilder = context.getWorld().bridge.get(IBridgeWorld.class).getChunkBuilder();
        ChunkDisplay display = BBS.getStructures().getCachedChunk(this.form.structure.get(), context, chunkBuilder);

        if (display != null)
        {
            Shader basic = context.getShaders().get(chunkBuilder.getAttributes());

            context.stack.push();
            context.stack.translate(-display.chunk.w / 2F, 0, -display.chunk.d / 2F);

            basic.bind();
            CommonShaderAccess.setModelView(basic, context.stack);

            context.getTextures().bind(chunkBuilder.models.atlas);
            display.render();

            context.stack.pop();
        }
    }
}