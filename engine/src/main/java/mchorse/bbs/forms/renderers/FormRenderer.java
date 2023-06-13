package mchorse.bbs.forms.renderers;

import mchorse.bbs.forms.forms.BodyPart;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.uniforms.Uniform;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.world.entities.Entity;

import java.util.Collections;
import java.util.List;

public abstract class FormRenderer <T extends Form>
{
    protected T form;

    public FormRenderer(T form)
    {
        this.form = form;
    }

    public T getForm()
    {
        return this.form;
    }

    public List<String> getBones()
    {
        return Collections.emptyList();
    }

    public abstract void renderUI(UIContext context, int x1, int y1, int x2, int y2);

    public final void render(Entity entity, RenderingContext context)
    {
        boolean isPicking = context instanceof UIRenderingContext && ((UIRenderingContext) context).getStencil().picking;

        context.stack.push();
        context.stack.multiply(this.form.transform.get(context.getTransition()).createMatrix());

        if (isPicking)
        {
            this.setupUniform((UIRenderingContext) context);
        }

        this.render3D(entity, context);

        if (isPicking)
        {
            this.handlePicking((UIRenderingContext) context);
        }

        this.renderBodyParts(entity, context);

        context.stack.pop();
    }

    protected void setupUniform(UIRenderingContext context)
    {
        for (Shader shader : context.getShaders().getAll())
        {
            Uniform pickerIndex = shader.getUniform("u_picker_index");

            if (pickerIndex instanceof UniformInt)
            {
                ((UniformInt) pickerIndex).set(context.getStencil().objectIndex);
            }
        }
    }

    protected void handlePicking(UIRenderingContext context)
    {
        context.getStencil().addPicking(this.form);
    }

    protected abstract void render3D(Entity entity, RenderingContext context);

    public void renderBodyParts(Entity target, RenderingContext context)
    {
        for (BodyPart part : this.form.parts.getAll())
        {
            part.render(target, context);
        }
    }
}