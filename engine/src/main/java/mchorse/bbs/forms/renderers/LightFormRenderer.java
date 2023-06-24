package mchorse.bbs.forms.renderers;

import mchorse.bbs.forms.forms.LightForm;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.lighting.Light;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LightFormRenderer extends FormRenderer<LightForm>
{
    public LightFormRenderer(LightForm form)
    {
        super(form);
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        int color = this.form.color.get(context.getTransition()).getARGBColor();

        context.batcher.dropCircleShadow((x1 + x2) / 2, (y1 + y2) / 2, Math.min(x2 - x1, y2 - y1) / 2, 12, Colors.setA(color, 1F), Colors.setA(color, 0F));
    }

    @Override
    protected void render3D(Entity entity, RenderingContext context)
    {
        Vector4f position = new Vector4f(0, 0, 0, 1);

        context.stack.getModelMatrix().transform(position);

        Vector3f finalPosition = new Vector3f(position.x, position.y, position.z);
        Color color = new Color().copy(this.form.color.get(context.getTransition()));
        float distance = this.form.distance.get(context.getTransition());

        context.getLights().addLight(new Light(finalPosition, color, distance));
    }
}