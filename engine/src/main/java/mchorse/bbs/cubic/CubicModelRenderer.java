package mchorse.bbs.cubic;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.render.CubicAxisRenderer;
import mchorse.bbs.cubic.render.CubicMatrixRenderer;
import mchorse.bbs.cubic.render.CubicRenderer;
import mchorse.bbs.cubic.render.CubicVAORenderer;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.utils.joml.Vectors;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class CubicModelRenderer
{
    private CubicModel model;

    private VAO vaoModel;
    private CubicMatrixRenderer renderer;
    private MatrixStack stack = new MatrixStack();

    public CubicModelRenderer(CubicModel model)
    {
        this.model = model;
    }

    public List<Matrix4f> getMatrices()
    {
        return this.renderer.matrices;
    }

    public void createVAO()
    {
        this.vaoModel = BBS.getVAOs().create();
        this.vaoModel.register(VBOAttributes.VERTEX_NORMAL_UV_RGBA_BONES);

        VAOBuilder builder = BBS.getRender().getVAO().setup(this.vaoModel, null);
        MatrixStack stack = new MatrixStack();
        CubicVAORenderer processor = new CubicVAORenderer(this.model.normals);

        Model model = this.model.model;
        CubicModelAnimator.resetPose(model);

        builder.begin();

        stack.push();
        CubicRenderer.processRenderModel(processor, builder, stack, model);
        stack.pop();

        builder.flush();

        this.renderer = new CubicMatrixRenderer(model);
    }

    public void applyTransforms()
    {
        this.stack.reset();
        CubicRenderer.processRenderModel(this.renderer, null, this.stack, this.model.model);
    }

    public void renderVAO(RenderingContext context, Shader shader)
    {
        if (this.vaoModel == null)
        {
            this.createVAO();
        }

        this.applyTransforms();

        CommonShaderAccess.setBones(shader, this.renderer.matrices);
        shader.bind();

        if (!this.model.culling)
        {
            GLStates.cullFaces(false);
        }

        if (this.model.overlap)
        {
            Camera camera = context.getCamera();
            float units = -100000F;

            /* Some really weird code that makes overlapping work with models, in some cases */
            if (camera != null)
            {
                Vector4f temp4f = Vectors.TEMP_4F;

                context.stack.getModelMatrix().transform(temp4f.set(0, 0, 0, 1F));

                Vectors.TEMP_3D.set(temp4f.x, temp4f.y, temp4f.z);

                float distance = (float) (Vectors.TEMP_3D.distance(0, 0, 0) / camera.far);

                distance = 1F - distance;
                distance = (float) Math.pow(distance, 40);

                if (distance > 0.95F)
                {
                    units = -3000F;
                }
                else
                {
                    units = -100F * distance;
                }
            }

            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset( 1F, Math.min(units, -1F));
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        this.vaoModel.bindForRender();
        this.vaoModel.render(GL11.GL_TRIANGLES);
        this.vaoModel.unbindForRender();

        if (this.model.overlap)
        {
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GLStates.setupDepthFunction3D();
        }

        if (!this.model.culling)
        {
            GLStates.cullFaces(true);
        }

        if (context.isDebug())
        {
            this.stack.reset();

            this.renderAxes(context);
        }
    }

    private void renderAxes(RenderingContext context)
    {
        GLStates.depthTest(false);

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);
        VAOBuilder builder = context.getVAO().setup(shader);

        CommonShaderAccess.setModelView(shader, context.stack);
        builder.begin();
        CubicRenderer.processRenderModel(new CubicAxisRenderer(), builder, this.stack, this.model.model);
        builder.render(GL11.GL_LINES);

        GLStates.depthTest(true);
    }
}