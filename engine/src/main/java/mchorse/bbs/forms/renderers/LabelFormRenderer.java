package mchorse.bbs.forms.renderers;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.forms.LabelForm;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.graphics.text.builders.ColoredTextBuilder3D;
import mchorse.bbs.graphics.text.builders.ITextBuilder;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.world.entities.Entity;

import java.util.List;

public class LabelFormRenderer extends FormRenderer<LabelForm>
{
    public LabelFormRenderer(LabelForm form)
    {
        super(form);
    }

    @Override
    public void renderUI(UIContext context, int x1, int y1, int x2, int y2)
    {
        int color = this.form.color.get(context.getTransition()).getARGBColor();

        context.batcher.wallText(this.getRenderer(this.form.font.get(), context.render), TextUtils.processColoredText(this.form.text.get()), x1 + 4, y1 + 4, color, x2 - x1 - 8);
    }

    @Override
    public void render3D(Entity entity, RenderingContext context)
    {
        context.stack.push();

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA);
        VAOBuilder builder = context.getVAO().setup(shader);
        float scale = 1F / 16F;

        context.stack.scale(scale, -scale, scale);
        CommonShaderAccess.setModelView(shader, context.stack);

        FontRenderer font = this.getRenderer(this.form.font.get(), context);

        font.bindTexture(context);

        GLStates.cullFaces(false);

        if (this.form.max.get(context.getTransition()) <= 10)
        {
            this.renderString(context, builder, font);
        }
        else
        {
            this.renderLimitedString(context, builder, font);
        }

        GLStates.cullFaces(true);

        context.stack.pop();
    }

    private void renderString(RenderingContext context, VAOBuilder builder, FontRenderer text)
    {
        String content = TextUtils.processColoredText(this.form.text.get());
        float transition = context.getTransition();
        int w = text.getWidth(content) - 1;
        int h = text.getHeight();
        int x = (int) (-w * this.form.anchorX.get(transition));
        int y = (int) (-h * this.form.anchorY.get(transition));

        builder.begin();

        Color color = this.form.shadowColor.get(transition);
        ColoredTextBuilder3D textBuilder = ITextBuilder.colored3D;

        if (color.a > 0)
        {
            textBuilder.setMultiplicative(true);
            text.buildVAO(x, y, content, builder, textBuilder.setup(color.getARGBColor(), this.form.shadowX.get(transition), this.form.shadowY.get(transition), -0.1F));
            textBuilder.setMultiplicative(false);
        }

        text.buildVAO(x, y, content, builder, textBuilder.setup(this.form.color.get(transition).getARGBColor()));
        builder.render();

        this.renderShadow(context, x, y, w, h);
    }

    private void renderLimitedString(RenderingContext context, VAOBuilder builder, FontRenderer text)
    {
        float transition = context.getTransition();
        int w = 0;
        int h = text.getHeight();
        String content = TextUtils.processColoredText(this.form.text.get());
        List<String> lines = text.split(content, this.form.max.get(transition));

        if (lines.size() <= 1)
        {
            this.renderString(context, builder, text);

            return;
        }

        for (String line : lines)
        {
            w = Math.max(text.getWidth(line) - 1, w);
            h += 12;
        }

        h -= 12;

        int x = (int) (-w * this.form.anchorX.get(transition));
        int y = (int) (-h * this.form.anchorY.get(transition));
        int y2 = y;

        builder.begin();

        Color shadow = this.form.shadowColor.get(transition);

        if (shadow.a > 0)
        {
            for (String line : lines)
            {
                int x2 = x + (this.form.anchorLines.get() ? (int) ((w - text.getWidth(line)) * this.form.anchorX.get(transition)) : 0);

                text.buildVAO(x2, y2, line, builder, ITextBuilder.colored3D.setup(shadow.getARGBColor(), this.form.shadowX.get(transition), this.form.shadowY.get(transition), -0.1F));

                y2 += 12;
            }

            y2 = y;
        }

        int color = this.form.color.get(transition).getARGBColor();

        for (String line : lines)
        {
            int x2 = x + (this.form.anchorLines.get() ? (int) ((w - text.getWidth(line)) * this.form.anchorX.get(transition)) : 0);

            text.buildVAO(x2, y2, line, builder, ITextBuilder.colored3D.setup(color));

            y2 += 12;
        }

        builder.render();

        this.renderShadow(context, x, y, w, h);
    }

    private void renderShadow(RenderingContext context, int x, int y, int w, int h)
    {
        float offset = this.form.offset.get(context.getTransition());
        Color color = this.form.background.get(context.getTransition());

        if (color.a <= 0)
        {
            return;
        }

        context.stack.push();
        context.stack.translate(0, 0, -0.2F);

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);
        VAOBuilder builder = context.getVAO().setup(shader);

        CommonShaderAccess.setModelView(shader, context.stack);
        builder.begin();

        Draw.fillQuad(
            builder,
            x + w + offset, y - offset, 0,
            x - offset, y - offset, 0,
            x - offset, y + h + offset, 0,
            x + w + offset, y + h + offset, 0,
            color.r, color.g, color.b, color.a
        );

        builder.render();
        context.stack.pop();
    }

    private FontRenderer getRenderer(Link fontLink, RenderingContext context)
    {
        if (fontLink == null)
        {
            return context.getFont();
        }

        FontRenderer fontRenderer = BBS.getFonts().getRenderer(fontLink);

        return fontRenderer == null ? context.getFont() : fontRenderer;
    }
}