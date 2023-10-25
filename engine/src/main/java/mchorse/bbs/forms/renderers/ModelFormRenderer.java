package mchorse.bbs.forms.renderers;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.cubic.CubicModelAnimator;
import mchorse.bbs.cubic.CubicModelRenderer;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.forms.forms.BodyPart;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelFormRenderer extends FormRenderer<ModelForm>
{
    private Matrix4f uiMatrix = new Matrix4f();
    private Map<String, Matrix4f> bones = new HashMap<>();

    private Entity entity = EntityArchitect.createDummy();

    public ModelFormRenderer(ModelForm form)
    {
        super(form);
    }

    @Override
    public List<String> getBones()
    {
        return new ArrayList<>(this.form.getModel().model.getAllGroupKeys());
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        context.batcher.flush();

        this.form.ensureAnimator();

        CubicModel model = this.form.getModel();

        if (this.form.getAnimator() != null && model != null)
        {
            context.render.stack.push();
            context.render.stack.getNormalMatrix().scale(1, -1, 1);

            float scale = (y2 - y1) / 2.5F;
            int x = x1 + (x2 - x1) / 2;
            float y = y1 + (y2 - y1) * 0.85F;

            this.uiMatrix.identity();
            this.uiMatrix.translate(x, y, 40);
            this.uiMatrix.scale(scale, -scale, scale);
            this.uiMatrix.rotateX(MathUtils.PI / 8);
            this.uiMatrix.rotateY(MathUtils.toRad(context.getTickTransition()));
            this.uiMatrix.mul(this.form.transform.get(context.getTransition()).createMatrix());

            Link link = this.form.texture.get(context.getTransition());
            Link texture = link == null ? model.texture : link;

            CubicModelAnimator.resetPose(model.model);

            this.form.getAnimator().applyActions(null, model.model, context.getTransition());
            this.form.getPose(context.getTransition()).apply(model.model);

            Shader shader = context.render.getShaders().get(VBOAttributes.VERTEX_NORMAL_UV_RGBA_BONES);

            context.render.stack.multiply(this.uiMatrix);

            CommonShaderAccess.setColor(shader, this.form.color.get(context.getTransition()));
            CommonShaderAccess.setModelView(shader, context.render.stack);

            GLStates.setupDepthFunction3D();

            context.render.getTextures().bind(texture);
            model.getRenderer().renderVAO(context.render, shader);

            CommonShaderAccess.resetColor(shader);

            /* Render body parts */
            this.captureMatrices(model);
            this.renderBodyParts(this.entity, context.render);

            GLStates.setupDepthFunction2D();

            context.render.stack.pop();
        }
    }

    @Override
    public void render3D(Entity entity, RenderingContext context)
    {
        this.form.ensureAnimator();

        CubicModel model = this.form.getModel();

        if (this.form.getAnimator() != null && model != null)
        {
            Link link = this.form.texture.get(context.getTransition());
            Link texture = link == null ? model.texture : link;

            CubicModelAnimator.resetPose(model.model);

            this.form.getAnimator().applyActions(entity, model.model, context.getTransition());
            this.form.getPose(context.getTransition()).apply(model.model);

            context.getTextures().bind(texture);

            Shader shader = context.getShaders().get(VBOAttributes.VERTEX_NORMAL_UV_RGBA_BONES);

            context.stack.rotateY(MathUtils.PI);
            CommonShaderAccess.setColor(shader, this.form.color.get(context.getTransition()));
            CommonShaderAccess.setModelView(shader, context.stack);

            CubicModelRenderer renderer = model.getRenderer();

            renderer.renderVAO(context, shader);

            CommonShaderAccess.resetColor(shader);

            this.captureMatrices(model);
        }
    }

    @Override
    protected void handlePicking(UIRenderingContext context)
    {
        CubicModel model = this.form.getModel();

        if (model == null || model.model == null)
        {
            return;
        }

        for (ModelGroup group : model.model.getOrderedGroups())
        {
            context.getStencil().addPicking(this.form, group.id);
        }
    }

    private void captureMatrices(CubicModel model)
    {
        List<Matrix4f> matrices = model.getRenderer().getMatrices();

        for (ModelGroup group : model.model.getAllGroups())
        {
            Matrix4f matrix = new Matrix4f(matrices.get(group.index));

            matrix.translate(
                group.initial.translate.x / 16,
                group.initial.translate.y / 16,
                group.initial.translate.z / 16
            );
            matrix.rotateY(MathUtils.PI);
            this.bones.put(group.id, matrix);
        }
    }

    @Override
    public void renderBodyParts(Entity target, RenderingContext context)
    {
        for (BodyPart part : this.form.parts.getAll())
        {
            Matrix4f matrix = this.bones.get(part.bone);

            context.stack.push();

            if (matrix != null)
            {
                context.stack.multiply(matrix);
            }
            else
            {
                context.stack.rotateY(MathUtils.PI);
            }

            part.render(target, context);

            context.stack.pop();
        }

        this.bones.clear();
    }

    @Override
    public void collectMatrices(Entity entity, MatrixStack stack, Map<String, Matrix4f> matrices, String prefix, float transition)
    {
        stack.push();
        stack.multiply(this.form.transform.get(transition).createMatrix());

        matrices.put(prefix, new Matrix4f(stack.getModelMatrix()));

        /* Collect bones and add them to matrix list */
        CubicModel model = this.form.getModel();

        if (this.form.getAnimator() != null && model != null)
        {
            CubicModelAnimator.resetPose(model.model);

            this.form.getAnimator().applyActions(entity, model.model, transition);
            this.form.getPose(transition).apply(model.model);

            stack.rotateY(MathUtils.PI);
            model.getRenderer().applyTransforms();
            this.captureMatrices(model);
        }

        for (Map.Entry<String, Matrix4f> entry : this.bones.entrySet())
        {
            stack.push();
            stack.multiply(entry.getValue());
            matrices.put(StringUtils.combinePaths(prefix, entry.getKey()), new Matrix4f(stack.getModelMatrix()));
            stack.pop();
        }

        int i = 0;

        /* Recursively do the same thing with body parts */
        for (BodyPart part : this.form.parts.getAll())
        {
            Form form = part.getForm();

            if (form != null)
            {
                Matrix4f matrix = this.bones.get(part.bone);

                stack.push();

                if (matrix != null)
                {
                    stack.multiply(matrix);
                }
                else
                {
                    stack.rotateY(MathUtils.PI);
                }

                stack.push();
                stack.multiply(part.getTransform().createMatrix());

                form.getRenderer().collectMatrices(entity, stack, matrices, StringUtils.combinePaths(prefix, String.valueOf(i)), transition);

                stack.pop();

                stack.pop();
            }

            i += 1;
        }

        stack.pop();

        this.bones.clear();
    }
}