package mchorse.bbs.forms.renderers;

import mchorse.bbs.forms.forms.ExtrudedForm;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.world.entities.Entity;

public class ExtrudedFormRenderer extends FormRenderer<ExtrudedForm>
{
    public ExtrudedFormRenderer(ExtrudedForm form)
    {
        super(form);
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        Link t = this.form.texture.get(context.getTransition());

        if (t == null)
        {
            return;
        }

        Texture texture = context.render.getTextures().getTexture(t);

        float min = Math.min(texture.width, texture.height);
        int ow = (x2 - x1) - 4;
        int oh = (y2 - y1) - 4;

        int w = (int) ((texture.width / min) * ow);
        int h = (int) ((texture.height / min) * ow);

        int x = x1 + (ow - w) / 2 + 2;
        int y = y1 + (oh - h) / 2 + 2;

        context.batcher.fullTexturedBox(texture, x, y, w, h);
    }

    @Override
    protected void render3D(Entity entity, RenderingContext context)
    {
        Link texture = this.form.texture.get(context.getTransition());
        VAO vao = context.getTextures().getExtruder().get(texture);

        if (vao != null)
        {
            Shader shader = context.getShaders().get(VBOAttributes.VERTEX_NORMAL_UV_RGBA);

            context.getTextures().bind(texture);
            CommonShaderAccess.setModelView(shader, context.stack);

            shader.bind();
            vao.bindForRender();
            vao.renderTriangles();
            vao.unbindForRender();
        }
    }
}