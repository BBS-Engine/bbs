package mchorse.bbs.ui.utils.renderers;

import mchorse.bbs.graphics.line.LineBuilder;
import mchorse.bbs.graphics.line.SolidColorLineRenderer;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.tooltips.styles.TooltipStyle;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.MathUtils;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class InterpolationRenderer
{
    public static void renderInterpolationPreview(IInterpolation interp, UIContext context, int x, int y, float anchorX, float anchorY, int duration)
    {
        if (interp == null)
        {
            return;
        }

        final float iterations = 40;
        final float padding = 50;

        int w = 140;
        int h = 130;

        TooltipStyle style = TooltipStyle.get();
        String tooltip = interp.getTooltip().get();
        List<String> lines = context.font.split(tooltip, w - 20);
        int ah = lines.isEmpty() ? 0 : lines.size() * (context.font.getHeight() + 4);

        y = MathUtils.clamp(y, 0, context.menu.height - h - ah);

        x -= (int) (w * anchorX);
        y -= (int) (h * anchorY);

        Area.SHARED.set(x, y, w, h + ah);
        style.renderBackground(context, Area.SHARED);

        Color fg = Colors.COLOR.set(style.getForegroundColor(), false);
        int font = style.getTextColor();

        fg.a = 0.2F;

        context.font.render(context.render, interp.getName().get(), x + 10, y + 10, font);

        for (int i = 0; i < lines.size(); i++)
        {
            context.font.render(context.render, lines.get(i), x + 10, y + h - 5 + i * (context.font.getHeight() + 4), font);
        }

        Shader shader = context.render.getShaders().get(VBOAttributes.VERTEX_RGBA_2D);
        VAOBuilder builder = context.render.getVAO().setup(shader);

        CommonShaderAccess.setModelView(shader);

        builder.begin();
        builder.xy(x + 10, y + 20).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + 10, y + h - 10).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w / 2, y + 20).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w / 2, y + h - 10).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w - 10, y + 20).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w - 10, y + h - 10).rgba(fg.r, fg.g, fg.b, fg.a);

        builder.xy(x + 10, y + 20).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w - 10, y + 20).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + 10, y + 20 + (h - 30) / 2).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w - 10, y + 20 + (h - 30) / 2).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + 10, y + h - 10).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w - 10, y + h - 10).rgba(fg.r, fg.g, fg.b, fg.a);

        builder.xy(x + 10, y + h - 10 - padding / 2).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w - 10, y + h - 10 - padding / 2).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + 10, y + 20 + padding / 2).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.xy(x + w - 10, y + 20 + padding / 2).rgba(fg.r, fg.g, fg.b, fg.a);
        builder.render(GL11.GL_LINES);

        fg.a = 1F;

        LineBuilder line = new LineBuilder(0.75F);

        for (int i = 0; i <= iterations; i++)
        {
            float factor = i / iterations;
            float value = 1 - interp.interpolate(0, 1, factor);

            float x1 = x + 10 + factor * (w - 20);
            float y1 = y + 20 + padding / 2 + value * (h - 30 - padding);

            line.add(x1, y1);
        }

        line.render(builder, SolidColorLineRenderer.get(fg));

        context.font.render(context.render, "A", x + 14, (int)(y + h - 10 - padding / 2) + 4, font);
        context.font.render(context.render, "B", x + w - 19, (int)(y + 20 + padding / 2) - context.font.getHeight() - 4, font);

        float tick = context.getTickTransition() % (duration + 20);
        float factor = MathUtils.clamp(tick / (float) duration, 0, 1);
        int px = x + w - 5;
        int py = y + 20 + (int) (padding / 2) + (int) ((1 - interp.interpolate(0, 1, factor)) * (h - 30 - padding));

        context.draw.box(px - 2, py - 2, px + 2, py + 2, Colors.A100 + fg.getRGBColor());
    }
}