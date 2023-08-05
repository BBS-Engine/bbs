package mchorse.bbs.graphics.text;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.text.builders.ColoredTextBuilder2D;
import mchorse.bbs.graphics.text.builders.ITextBuilder;
import mchorse.bbs.graphics.text.format.IFontFormat;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAOBuilder;
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
                    float skew = this.context.skew * (glyph.height / (float) font.height);

                    Vector2f p = this.process(stack, rx + skew, ry);
                    float x1 = p.x;
                    float y1 = p.y;

                    p = this.process(stack, rx, ry + glyph.height);
                    float x2 = p.x;
                    float y2 = p.y;

                    p = this.process(stack, rx + glyph.width, ry + glyph.height);
                    float x3 = p.x;
                    float y3 = p.y;

                    p = this.process(stack, rx + glyph.width + skew, ry);
                    float x4 = p.x;
                    float y4 = p.y;

                    textBuilder.put(builder, x1, y1, glyph.x, glyph.y, tw, th, color);
                    textBuilder.put(builder, x2, y2, glyph.x, glyph.y + glyph.height, tw, th, color);
                    textBuilder.put(builder, x3, y3, glyph.x + glyph.width, glyph.y + glyph.height, tw, th, color);
                    textBuilder.put(builder, x4, y4, glyph.x + glyph.width, glyph.y, tw, th, color);

                    if (!builder.hasIndex())
                    {
                        textBuilder.put(builder, x1, y1, glyph.x, glyph.y, tw, th, color);
                        textBuilder.put(builder, x3, y3, glyph.x + glyph.width, glyph.y + glyph.height, tw, th, color);
                    }

                    if (this.context.bold)
                    {
                        p = this.process(stack, rx + skew + 1, ry);
                        float bx1 = p.x;
                        float by1 = p.y;

                        p = this.process(stack, rx + 1, ry + glyph.height);
                        float bx2 = p.x;
                        float by2 = p.y;

                        p = this.process(stack, rx + glyph.width + 1, ry + glyph.height);
                        float bx3 = p.x;
                        float by3 = p.y;

                        p = this.process(stack, rx + glyph.width + skew + 1, ry);
                        float bx4 = p.x;
                        float by4 = p.y;

                        textBuilder.put(builder, bx1, by1, glyph.x, glyph.y, tw, th, color);
                        textBuilder.put(builder, bx2, by2, glyph.x, glyph.y + glyph.height, tw, th, color);
                        textBuilder.put(builder, bx3, by3, glyph.x + glyph.width, glyph.y + glyph.height, tw, th, color);
                        textBuilder.put(builder, bx4, by4, glyph.x + glyph.width, glyph.y, tw, th, color);

                        if (!builder.hasIndex())
                        {
                            textBuilder.put(builder, bx1, by1, glyph.x, glyph.y, tw, th, color);
                            textBuilder.put(builder, bx3, by3, glyph.x + glyph.width, glyph.y + glyph.height, tw, th, color);
                        }
                    }

                    if (builder.hasIndex())
                    {
                        builder.index(j * VERTICES_PER_QUAD);
                        builder.index(j * VERTICES_PER_QUAD + 1);
                        builder.index(j * VERTICES_PER_QUAD + 2);
                        builder.index(j * VERTICES_PER_QUAD + 3);
                        builder.index(j * VERTICES_PER_QUAD);
                        builder.index(j * VERTICES_PER_QUAD + 2);

                        if (this.context.bold)
                        {
                            j += 1;

                            builder.index(j * VERTICES_PER_QUAD);
                            builder.index(j * VERTICES_PER_QUAD + 1);
                            builder.index(j * VERTICES_PER_QUAD + 2);
                            builder.index(j * VERTICES_PER_QUAD + 3);
                            builder.index(j * VERTICES_PER_QUAD);
                            builder.index(j * VERTICES_PER_QUAD + 2);
                        }
                    }

                    color = this.context.color;

                    j += 1;
                }

                x += glyph.advance + 1 + (this.context.bold ? 1 : 0);
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
        boolean bold = false;

        for (int i = 0, c = str.length(); i < c; i++)
        {
            char character = str.charAt(i);
            Glyph glyph = this.font.getGlyph(character);

            if (glyph == null || character == FORMATTING_CHARACTER || previous == FORMATTING_CHARACTER)
            {
                if (previous == FORMATTING_CHARACTER && character == this.font.boldChar)
                {
                    bold = true;
                }
                if (previous == FORMATTING_CHARACTER && character == this.font.resetChar)
                {
                    bold = false;
                }

                previous = character;

                continue;
            }

            x += this.font.getKerning(previous, character) + glyph.advance + 1 + (bold ? 1 : 0);
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
}