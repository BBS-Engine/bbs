package mchorse.bbs.forms.renderers;

import mchorse.bbs.forms.forms.BillboardForm;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.Quad;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BillboardFormRenderer extends FormRenderer<BillboardForm>
{
    private static final Quad quad = new Quad();
    private static final Quad uvQuad = new Quad();

    private static final Matrix4f matrix = new Matrix4f();

    public BillboardFormRenderer(BillboardForm form)
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

        texture.bind();
        context.draw.fullTexturedBox(x, y, w, h);
    }

    @Override
    public void render3D(Entity entity, RenderingContext context)
    {
        Link t = this.form.texture.get(context.getTransition());

        if (t == null)
        {
            return;
        }

        Texture texture = context.getTextures().getTexture(t);

        float w = texture.width;
        float h = texture.height;
        float ow = w;
        float oh = h;

        /* TL = top left, BR = bottom right*/
        Vector4f crop = this.form.crop.get(context.getTransition());
        float uvTLx = crop.x / w;
        float uvTLy = crop.y / h;
        float uvBRx = 1 - crop.z / w;
        float uvBRy = 1 - crop.w / h;

        uvQuad.p1.set(uvTLx, uvTLy, 0);
        uvQuad.p2.set(uvBRx, uvTLy, 0);
        uvQuad.p3.set(uvTLx, uvBRy, 0);
        uvQuad.p4.set(uvBRx, uvBRy, 0);

        float uvFinalTLx = uvTLx;
        float uvFinalTLy = uvTLy;
        float uvFinalBRx = uvBRx;
        float uvFinalBRy = uvBRy;

        if (this.form.resizeCrop.get(context.getTransition()))
        {
            uvFinalTLx = uvFinalTLy = 0F;
            uvFinalBRx = uvFinalBRy = 1F;

            w = w - crop.x - crop.z;
            h = h - crop.y - crop.w;
        }

        /* Calculate quad's size (vertices, not UV) */
        float ratioX = w > h ? h / w : 1F;
        float ratioY = h > w ? w / h : 1F;
        float TLx = (uvFinalTLx - 0.5F) * ratioY;
        float TLy = -(uvFinalTLy - 0.5F) * ratioX;
        float BRx = (uvFinalBRx - 0.5F) * ratioY;
        float BRy = -(uvFinalBRy - 0.5F) * ratioX;

        quad.p1.set(TLx, TLy, 0);
        quad.p2.set(BRx, TLy, 0);
        quad.p3.set(TLx, BRy, 0);
        quad.p4.set(BRx, BRy, 0);

        float offsetX = this.form.offsetX.get(context.getTransition());
        float offsetY = this.form.offsetY.get(context.getTransition());
        float rotation = this.form.rotation.get(context.getTransition());

        if (offsetX != 0F || offsetY != 0F || rotation != 0F)
        {
            float centerX = (crop.x + (ow - crop.z)) / 2F / ow;
            float centerY = (crop.y + (oh - crop.w)) / 2F / ow;

            matrix.identity()
                .translate(centerX, centerY, 0)
                .rotateZ(MathUtils.toRad(rotation))
                .translate(offsetX / ow, offsetY / oh, 0)
                .translate(-centerX, -centerY, 0);

            uvQuad.transform(matrix);
        }

        this.renderQuad(context);
    }

    private void renderQuad(RenderingContext context)
    {
        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_NORMAL_UV_RGBA);
        VAOBuilder builder = context.getVAO().setup(shader);
        Color color = this.form.color.get(context.getTransition());

        if (this.form.billboard.get(context.getTransition()))
        {
            Matrix4f modelMatrix = context.stack.getModelMatrix();
            Vector3f scale = Vectors.TEMP_3F;
            Matrix4f invert = Matrices.TEMP_4F.set(context.getCamera().view).invert();

            modelMatrix.getScale(scale);

            modelMatrix.m00(1).m01(0).m02(0);
            modelMatrix.m10(0).m11(1).m12(0);
            modelMatrix.m20(0).m21(0).m22(1);

            modelMatrix.mul(invert);
            modelMatrix.scale(scale);

            context.stack.getNormalMatrix().identity().mul(Matrices.TEMP_3F.set(invert));
        }

        CommonShaderAccess.setModelView(shader, context.stack);
        CommonShaderAccess.resetColor(shader);

        context.getTextures().bind(this.form.texture.get(context.getTransition()));

        GLStates.cullFaces(false);

        builder.begin();
        builder.xyz(quad.p1.x, quad.p1.y, 0F).normal(0F, 0F, 1F).uv(uvQuad.p1.x, uvQuad.p1.y).rgba(color.r, color.g, color.b, color.a);
        builder.xyz(quad.p2.x, quad.p2.y, 0F).normal(0F, 0F, 1F).uv(uvQuad.p2.x, uvQuad.p2.y).rgba(color.r, color.g, color.b, color.a);
        builder.xyz(quad.p3.x, quad.p3.y, 0F).normal(0F, 0F, 1F).uv(uvQuad.p3.x, uvQuad.p3.y).rgba(color.r, color.g, color.b, color.a);
        builder.xyz(quad.p2.x, quad.p2.y, 0F).normal(0F, 0F, 1F).uv(uvQuad.p2.x, uvQuad.p2.y).rgba(color.r, color.g, color.b, color.a);
        builder.xyz(quad.p4.x, quad.p4.y, 0F).normal(0F, 0F, 1F).uv(uvQuad.p4.x, uvQuad.p4.y).rgba(color.r, color.g, color.b, color.a);
        builder.xyz(quad.p3.x, quad.p3.y, 0F).normal(0F, 0F, 1F).uv(uvQuad.p3.x, uvQuad.p3.y).rgba(color.r, color.g, color.b, color.a);
        builder.render();

        GLStates.cullFaces(true);
    }
}