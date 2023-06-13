package mchorse.bbs.graphics.text;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.text.builders.ColoredTextBuilder2D;
import mchorse.bbs.graphics.text.builders.ITextBuilder;
import mchorse.bbs.graphics.text.format.IFontFormat;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

public class FontRenderer
{
    public static final char FORMATTING_CHARACTER = '§';
    public static final String FORMATTING_STRING = "§";

    private static final Color WHITE = Color.white();

    public Font font;
    public Link texture;

    private FontRendererContext context = new FontRendererContext();
    private int w;
    private int h;

    private TextSplitter splitter = new TextSplitter();
    private Vector4f vector4 = new Vector4f();
    private Vector2f vector2 = new Vector2f();

    public FontRenderer(Link texture, Font font)
    {
        this.texture = texture;
        this.font = font;

        this.updateSize();
    }

    private Vector2f process(MatrixStack stack, float x, float y)
    {
        if (stack == null)
        {
            return this.vector2.set(x, y);
        }

        this.vector4.set(x, y, 0F, 1F);

        stack.getModelMatrix().transform(this.vector4);

        return this.vector2.set(this.vector4.x, this.vector4.y);
    }

    private void updateSize()
    {
        Texture t = BBS.getTextures().getTexture(this.texture);

        this.w = t.width;
        this.h = t.height;
    }

    public void setTime(float time)
    {
        this.context.time = time;
    }

    public void update(FontRenderer newFontRenderer)
    {
        this.font = newFontRenderer.font;
        this.texture = newFontRenderer.texture;

        this.updateSize();
    }

    public int build(VAOBuilder builder, String label, int x, int y, int index, int c, boolean shadow)
    {
        return this.build(null, builder, label, x, y, index, c, shadow);
    }

    public int build(MatrixStack stack, VAOBuilder builder, String label, int x, int y, int index, int c, boolean shadow)
    {
        if (Colors.getAlpha(c) <= 0F)
        {
            c = Colors.setA(c, 1F);
        }

        ColoredTextBuilder2D textBuilder = ITextBuilder.colored2D;

        if (shadow)
        {
            int shadowColor = Colors.mulRGB(c, 0.15F);

            textBuilder.setMultiplicative(true);
            index = this.buildVAO(stack, x, y + 1, label, builder, textBuilder.color(shadowColor), index);
            textBuilder.setMultiplicative(false);
        }

        return this.buildVAO(stack, x, y, label, builder, textBuilder.color(c), index);
    }

    public int buildVAO(int lx, int ly, String text, VAOBuilder builder, ITextBuilder textBuilder)
    {
        return this.buildVAO(null, lx, ly, text, builder, textBuilder, 0);
    }

    public int buildVAO(MatrixStack stack, int lx, int ly, String text, VAOBuilder builder, ITextBuilder textBuilder, int j)
    {
        final int VERTICES_PER_QUAD = 4;

        float tw = this.w;
        float th = this.h;

        int x = 0;
        int y = 0;
        int h = this.getHeight();
        char prev = 0;
        Color color = this.context.color;

        this.context.reset();

        for (int i = 0, c = text.length(); i < c; i++)
        {
            char letter = text.charAt(i);

            if (prev == FORMATTING_CHARACTER)
            {
                IFontFormat format = this.font.formats.get((int) letter);

                if (format != null)
                {
                    format.apply(this.context);
                }

                prev = letter;

                continue;
            }

            Glyph glyph = this.font.getGlyph(letter);

            if (glyph != null)
            {
                x += this.font.getKerning(prev, letter);

                if (glyph.width != 0)
                {
                    this.context.setup(i,
                        lx + x + glyph.offsetX,
                        ly + y + glyph.offsetY + h
                    );

                    for (IFontFormat format : this.context.activeFormats)
                    {
                        format.process(context);
                    }

                    float rx = this.context.x;
                    float ry = this.context.y;

                    if (glyph.emoji)
                    {
                        color = WHITE;
                    }

                    /* TL, BL, BR, TR vertices */
                    Vector2f p = this.process(stack, rx + this.context.skew, ry);
                    float x1 = p.x;
                    float y1 = p.y;

                    p = this.process(stack, rx, ry + glyph.height);
                    float x2 = p.x;
                    float y2 = p.y;

                    p = this.process(stack, rx + glyph.width, ry + glyph.height);
                    float x3 = p.x;
                    float y3 = p.y;

                    p = this.process(stack, rx + glyph.width + this.context.skew, ry);
                    float x4 = p.x;
                    float y4 = p.y;

                    textBuilder.put(builder, x1, y1, glyph.x, glyph.y, tw, th, color);
                    textBuilder.put(builder, x2, y2, glyph.x, glyph.y + glyph.height, tw, th, color);
                    textBuilder.put(builder, x3, y3, glyph.x + glyph.width, glyph.y + glyph.height, tw, th, color);
                    textBuilder.put(builder, x4, y4, glyph.x + glyph.width, glyph.y, tw, th, color);

                    if (builder.hasIndex())
                    {
                        builder.index(j * VERTICES_PER_QUAD);
                        builder.index(j * VERTICES_PER_QUAD + 1);
                        builder.index(j * VERTICES_PER_QUAD + 2);
                        builder.index(j * VERTICES_PER_QUAD + 3);
                        builder.index(j * VERTICES_PER_QUAD);
                        builder.index(j * VERTICES_PER_QUAD + 2);
                    }
                    else
                    {
                        textBuilder.put(builder, x1, y1, glyph.x, glyph.y, tw, th, color);
                        textBuilder.put(builder, x3, y3, glyph.x + glyph.width, glyph.y + glyph.height, tw, th, color);
                    }

                    color = this.context.color;

                    j++;
                }

                x += glyph.advance + 1;
            }

            prev = letter;
        }

        return j;
    }

    public String limitToWidth(String str, int width)
    {
        return this.limitToWidth(str, "...", width);
    }

    public String limitToWidth(String str, String suffix, int width)
    {
        if (str.isEmpty())
        {
            return str;
        }

        int w = this.getWidth(str);

        if (w < width)
        {
            return str;
        }

        int sw = this.getWidth(suffix);
        int i = str.length() - 1;

        while (w + sw >= width && i > 0)
        {
            w -= this.getWidth(str.charAt(i));
            i -= 1;
        }

        str = str.substring(0, i);

        return str.isEmpty() ? str : str + suffix;
    }

    /**
     * Split given string according to the given width
     */
    public List<String> split(String str, int width)
    {
        return this.splitter.split(this, str, width);
    }

    public boolean hasCharacter(char character)
    {
        return this.font.getGlyph(character) != null;
    }

    /**
     * Calculate the width of given string (new lines are ignored)
     */
    public int getWidth(String str)
    {
        char previous = 0;
        int x = 0;

        for (int i = 0, c = str.length(); i < c; i++)
        {
            char character = str.charAt(i);
            Glyph glyph = this.font.getGlyph(character);

            if (glyph == null || character == FORMATTING_CHARACTER || previous == FORMATTING_CHARACTER)
            {
                previous = character;

                continue;
            }

            x += this.font.getKerning(previous, character) + glyph.advance + 1;
            previous = character;
        }

        return x;
    }

    public int getWidth(char character)
    {
        return this.getWidth(character, '\0');
    }

    public int getWidth(char character, char previous)
    {
        if (character == FORMATTING_CHARACTER || previous == FORMATTING_CHARACTER)
        {
            return 0;
        }

        Glyph glyph = this.font.getGlyph(character);

        if (glyph == null)
        {
            return 0;
        }

        return this.font.getKerning(previous, character) + glyph.advance + 1;
    }

    public int getHeight()
    {
        return this.font.height;
    }

    public void bindTexture(RenderingContext context)
    {
        context.getTextures().bind(this.texture);
    }

    public void renderWithShadow(RenderingContext context, String label, int x, int y)
    {
        this.render(context, label, x, y, Colors.WHITE, true);
    }

    public void renderWithShadow(RenderingContext context, String label, int x, int y, int c)
    {
        this.render(context, label, x, y, c, true);
    }

    public void render(RenderingContext context, String label, int x, int y)
    {
        this.render(context, label, x, y, Colors.WHITE, false);
    }

    public void render(RenderingContext context, String label, int x, int y, int c)
    {
        this.render(context, label, x, y, c, false);
    }

    public void render(RenderingContext context, String label, int x, int y, int c, boolean shadow)
    {
        this.bindTexture(context);

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D);
        VAOBuilder builder = context.getVAO().setup(shader, VAO.DATA, VAO.INDICES);

        CommonShaderAccess.resetColor(shader);

        builder.begin();
        this.build(builder, label, x, y, 0, c, shadow);
        builder.render();
    }

    public int renderCentered(RenderingContext context, String label, int x, int y)
    {
        return this.renderCentered(context, label, x, y, Colors.WHITE, true);
    }

    public int renderCentered(RenderingContext context, String label, int x, int y, int color)
    {
        return this.renderCentered(context, label, x, y, color, true);
    }

    public int renderCentered(RenderingContext context, String label, int x, int y, int color, boolean shadow)
    {
        return this.renderAnchored(context, label, x, y, color, shadow, 0.5F, 0);
    }

    public int renderAnchored(RenderingContext context, String label, int x, int y, int color, boolean shadow, float ax, float ay)
    {
        int w = this.getWidth(label);

        this.render(context, label, (int) (x - ax * w), (int) (y + ay * this.getHeight()), color, shadow);

        return w;
    }
}