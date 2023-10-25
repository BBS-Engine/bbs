package mchorse.bbs.ui.forms.editors.utils;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.forms.editors.UIFormEditor;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.StencilFormFramebuffer;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class UIPickableFormRenderer extends UIFormRenderer
{
    public UIFormEditor formEditor;

    private boolean update;

    private StencilFormFramebuffer stencil = new StencilFormFramebuffer();

    public UIPickableFormRenderer(UIFormEditor formEditor)
    {
        this.formEditor = formEditor;
    }

    public void updatable()
    {
        this.update = true;
    }

    private void ensureFramebuffer()
    {
        this.stencil.setup(Link.bbs("stencil_form"));
        this.stencil.resizeGUI(this.area.w, this.area.h);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.ensureFramebuffer();
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.stencil.hasPicked() && context.mouseButton == 1)
        {
            Pair<Form, String> pair = this.stencil.getPicked();

            if (pair != null)
            {
                this.formEditor.pickFormFromRenderer(pair);

                return true;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected void renderUserModel(UIContext context)
    {
        if (this.form == null)
        {
            return;
        }

        this.form.getRenderer().render(this.entity, context.render);

        if (this.form.hitbox.get())
        {
            this.renderFormHitbox(context.render);
        }

        this.renderAxes(context);

        if (this.area.isInside(context))
        {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            this.stencil.apply(context);
            this.form.getRenderer().render(this.entity, context.render);

            this.stencil.pickGUI(context, this.area);
            this.stencil.unbind(context);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }
        else
        {
            this.stencil.clearPicking();
        }
    }

    private void renderAxes(UIContext context)
    {
        Matrix4f matrix = this.formEditor.editor.getOrigin(context.getTransition());
        final float axisSize = 0.1F;
        final float axisOffset = 0.005F;
        final float outlineSize = axisSize + 0.005F;
        final float outlineOffset = axisOffset + 0.005F;

        Shader shader = context.render.getShaders().get(VBOAttributes.VERTEX_RGBA);

        context.render.stack.push();

        if (matrix != null)
        {
            context.render.stack.multiply(matrix);
        }

        CommonShaderAccess.setModelView(shader, context.render.stack);

        context.render.stack.pop();

        /* Draw axes */
        VAOBuilder builder = context.render.getVAO().setup(shader);

        GLStates.depthTest(false);

        builder.begin();

        Draw.fillBox(builder, 0, -outlineOffset, -outlineOffset, outlineSize, outlineOffset, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, 0, -outlineOffset, outlineOffset, outlineSize, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, -outlineOffset, 0, outlineOffset, outlineOffset, outlineSize, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, -outlineOffset, -outlineOffset, outlineOffset, outlineOffset, outlineOffset, 0, 0, 0);

        Draw.fillBox(builder, 0, -axisOffset, -axisOffset, axisSize, axisOffset, axisOffset, 1, 0, 0);
        Draw.fillBox(builder, -axisOffset, 0, -axisOffset, axisOffset, axisSize, axisOffset, 0, 1, 0);
        Draw.fillBox(builder, -axisOffset, -axisOffset, 0, axisOffset, axisOffset, axisSize, 0, 0, 1);
        Draw.fillBox(builder, -axisOffset, -axisOffset, -axisOffset, axisOffset, axisOffset, axisOffset, 1, 1, 1);

        builder.render();

        GLStates.depthTest(true);
    }

    private void renderFormHitbox(RenderingContext context)
    {
        float hitboxW = this.form.hitboxWidth.get();
        float hitboxH = this.form.hitboxHeight.get();
        float eyeHeight = hitboxH * this.form.hitboxEyeHeight.get();

        /* Draw look vector */
        final float thickness = 0.01F;
        Draw.renderBox(context, -thickness, -thickness + eyeHeight, -thickness, thickness, thickness, 2F, 1F, 0F, 0F);

        /* Draw hitbox */
        Draw.renderBox(context, -hitboxW / 2, 0, -hitboxW / 2, hitboxW, hitboxH, hitboxW);
    }

    @Override
    protected void update()
    {
        super.update();

        if (this.update)
        {
            this.form.update(this.entity);
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (!this.stencil.hasPicked())
        {
            return;
        }

        int index = this.stencil.getIndex();
        Texture texture = this.stencil.getFramebuffer().getMainTexture();
        Pair<Form, String> pair = this.stencil.getPicked();
        int w = texture.width;
        int h = texture.height;

        Shader shader = context.render.getPickingShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D);

        CommonShaderAccess.setTarget(shader, index);
        context.batcher.texturedBox(shader, texture, Colors.WHITE, this.area.x, this.area.y, this.area.w, this.area.h, 0, h, w, 0, w, h);

        if (pair != null)
        {
            String label = pair.a.getIdOrName();

            if (!pair.b.isEmpty())
            {
                label += " - " + pair.b;
            }

            context.batcher.textCard(context.font, label, context.mouseX + 12, context.mouseY + 8);
        }
    }
}