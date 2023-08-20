package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Stack;

public class Batcher2D
{
    private static final Color c1 = new Color();
    private static final Color c2 = new Color();
    private static final Color c3 = new Color();
    private static final Color c4 = new Color();

    private RenderingContext context;
    private VAOBuilder builder;

    private int mode;
    private Shader shader;
    private Texture texture;

    private Stack<Area> scissors = new Stack<>();

    public Batcher2D(RenderingContext context)
    {
        this.context = context;
    }

    public RenderingContext getContext()
    {
        return this.context;
    }

    /* Screen space clipping */

    public void clip(Area area, UIContext context)
    {
        this.clip(area.x, area.y, area.w, area.h, context);
    }

    public void clip(int x, int y, int w, int h, UIContext context)
    {
        this.clip(context.globalX(x), context.globalY(y), w, h, context.menu.width, context.menu.height);
    }

    /**
     * Scissor (clip) the screen
     */
    public void clip(int x, int y, int w, int h, int sw, int sh)
    {
        Area scissor = this.scissors.isEmpty() ? null : this.scissors.peek();

        /* If it was scissored before, then clamp to the bounds of the last one */
        if (scissor != null)
        {
            w += Math.min(x - scissor.x, 0);
            h += Math.min(y - scissor.y, 0);
            x = MathUtils.clamp(x, scissor.x, scissor.ex());
            y = MathUtils.clamp(y, scissor.y, scissor.ey());
            w = MathUtils.clamp(w, 0, scissor.ex() - x);
            h = MathUtils.clamp(h, 0, scissor.ey() - y);
        }

        this.flush();

        scissor = new Area(x, y, w, h);
        this.scissorArea(x, y, w, h, sw, sh);
        this.scissors.add(scissor);
    }

    private void scissorArea(int x, int y, int w, int h, int sw, int sh)
    {
        /* Clipping area around scroll area */
        float rx = (float) Math.round(Window.width / (double) sw);
        float ry = (float) Math.round(Window.height / (double) sh);

        int xx = (int) (x * rx);
        int yy = (int) (Window.height - (y + h) * ry);
        int ww = (int) (w * rx);
        int hh = (int) (h * ry);

        GLStates.scissorTest(true);

        if (ww == 0 || hh == 0)
        {
            GL11.glScissor(0, 0, 1, 1);
        }
        else
        {
            GLStates.scissor(xx, yy, ww, hh);
        }
    }

    public void unclip(UIContext context)
    {
        this.unclip(context.menu.width, context.menu.height);
    }

    public void unclip(int sw, int sh)
    {
        this.flush();
        this.scissors.pop();

        if (this.scissors.isEmpty())
        {
            GLStates.scissorTest(false);
        }
        else
        {
            Area area = this.scissors.peek();

            this.scissorArea(area.x, area.y, area.w, area.h, sw, sh);
        }
    }

    /* Solid rectangles */

    public void normalizedBox(float x1, float y1, float x2, float y2, int color)
    {
        float temp = x1;

        x1 = Math.min(x1, x2);
        x2 = Math.max(temp, x2);

        temp = y1;

        y1 = Math.min(y1, y2);
        y2 = Math.max(temp, y2);

        this.box(x1, y1, x2, y2, color);
    }

    public void box(float x1, float y1, float x2, float y2, int color)
    {
        this.box(x1, y1, x2 - x1, y2 - y1, color, color, color, color);
    }

    public void box(float x, float y, float w, float h, int color1, int color2, int color3, int color4)
    {
        this.begin(VBOAttributes.VERTEX_RGBA_2D);

        c1.set(color1);
        c2.set(color2);
        c3.set(color3);
        c4.set(color4);

        /* c1 ---- c2
         * |        |
         * c3 ---- c4 */
        this.builder.xy(x, y).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x, y + h).rgba(c3.r, c3.g, c3.b, c3.a);
        this.builder.xy(x + w, y + h).rgba(c4.r, c4.g, c4.b, c4.a);
        this.builder.xy(x, y).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x + w, y + h).rgba(c4.r, c4.g, c4.b, c4.a);
        this.builder.xy(x + w, y).rgba(c2.r, c2.g, c2.b, c2.a);
    }

    public void dropShadow(int left, int top, int right, int bottom, int offset, int opaque, int shadow)
    {
        this.begin(VBOAttributes.VERTEX_RGBA_2D);

        left -= offset;
        top -= offset;
        right += offset;
        bottom += offset;

        c1.set(opaque);
        c2.set(shadow);

        /* Draw opaque part */
        this.builder.xy(right - offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(left + offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(right - offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(left + offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);

        /* Draw top shadow */
        this.builder.xy(right, top).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(left, top).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(right, top).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(right - offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);

        /* Draw bottom shadow */
        this.builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(left + offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(left, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(left, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(right, bottom).rgba(c2.r, c2.g, c2.b, c2.a);

        /* Draw left shadow */
        this.builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(left, top).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(left, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(left, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(left + offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);

        /* Draw right shadow */
        this.builder.xy(right, top).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(right - offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(right, top).rgba(c2.r, c2.g, c2.b, c2.a);
        this.builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(right, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
    }

    /* Gradients */

    public void gradientHBox(float x1, float y1, float x2, float y2, int leftColor, int rightColor)
    {
        this.box(x1, y1, x2 - x1, y2 - y1, leftColor, rightColor, leftColor, rightColor);
    }

    public void gradientVBox(float x1, float y1, float x2, float y2, int topColor, int bottomColor)
    {
        this.box(x1, y1, x2 - x1, y2 - y1, topColor, topColor, bottomColor, bottomColor);
    }

    public void dropCircleShadow(int x, int y, int radius, int segments, int opaque, int shadow)
    {
        c1.set(opaque);
        c2.set(shadow);

        this.flush();

        VAOBuilder builder = this.begin(GL11.GL_TRIANGLE_FAN, VBOAttributes.VERTEX_RGBA_2D, null);

        builder.xy(x, y).rgba(c1.r, c1.g, c1.b, c1.a);

        for (int i = 0; i <= segments; i ++)
        {
            double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

            builder.xy((float) (x - Math.cos(a) * radius), (float) (y + Math.sin(a) * radius)).rgba(c2.r, c2.g, c2.b, c2.a);
        }
    }

    public void dropCircleShadow(int x, int y, int radius, int offset, int segments, int opaque, int shadow)
    {
        if (offset >= radius)
        {
            this.dropCircleShadow(x, y, radius, segments, opaque, shadow);

            return;
        }

        c1.set(opaque);
        c2.set(shadow);

        this.flush();

        VAOBuilder builder = this.begin(GL11.GL_TRIANGLE_FAN, VBOAttributes.VERTEX_RGBA_2D, null);

        /* Draw opaque base */
        builder.xy(x, y).rgba(c1.r, c1.g, c1.b, c1.a);

        for (int i = 0; i <= segments; i ++)
        {
            double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

            builder.xy((int) (x - Math.cos(a) * offset), (int) (y + Math.sin(a) * offset)).rgba(c1.r, c1.g, c1.b, c1.a);
        }

        /* Draw outer shadow */
        builder = this.begin(VBOAttributes.VERTEX_RGBA_2D);

        for (int i = 0; i < segments; i ++)
        {
            double alpha1 = i / (double) segments * Math.PI * 2 - Math.PI / 2;
            double alpha2 = (i + 1) / (double) segments * Math.PI * 2 - Math.PI / 2;

            builder.xy((float) (x - Math.cos(alpha2) * offset), (float) (y + Math.sin(alpha2) * offset)).rgba(c1.r, c1.g, c1.b, c1.a);
            builder.xy((float) (x - Math.cos(alpha1) * offset), (float) (y + Math.sin(alpha1) * offset)).rgba(c1.r, c1.g, c1.b, c1.a);
            builder.xy((float) (x - Math.cos(alpha1) * radius), (float) (y + Math.sin(alpha1) * radius)).rgba(c2.r, c2.g, c2.b, c2.a);
            builder.xy((float) (x - Math.cos(alpha2) * offset), (float) (y + Math.sin(alpha2) * offset)).rgba(c1.r, c1.g, c1.b, c1.a);
            builder.xy((float) (x - Math.cos(alpha1) * radius), (float) (y + Math.sin(alpha1) * radius)).rgba(c2.r, c2.g, c2.b, c2.a);
            builder.xy((float) (x - Math.cos(alpha2) * radius), (float) (y + Math.sin(alpha2) * radius)).rgba(c2.r, c2.g, c2.b, c2.a);
        }
    }

    /* Outline methods */

    public void outlineCenter(float x, float y, float offset, int color)
    {
        this.outlineCenter(x, y, offset, color, 1);
    }

    public void outlineCenter(float x, float y, float offset, int color, int border)
    {
        this.outline(x - offset, y - offset, x + offset, y + offset, color, border);
    }

    public void outline(float x1, float y1, float x2, float y2, int color)
    {
        this.outline(x1, y1, x2, y2, color, 1);
    }

    /**
     * Draw rectangle outline with given border.
     */
    public void outline(float x1, float y1, float x2, float y2, int color, int border)
    {
        this.box(x1, y1, x1 + border, y2, color);
        this.box(x2 - border, y1, x2, y2, color);
        this.box(x1 + border, y1, x2 - border, y1 + border, color);
        this.box(x1 + border, y2 - border, x2 - border, y2, color);
    }

    /* Icon */

    public void icon(Icon icon, float x, float y)
    {
        this.icon(icon, Colors.WHITE, x, y);
    }

    public void icon(Icon icon, int color, float x, float y)
    {
        this.icon(icon, color, x, y, 0F, 0F);
    }

    public void icon(Icon icon, float x, float y, float ax, float ay)
    {
        this.icon(icon, Colors.WHITE, x, y, ax, ay);
    }

    public void icon(Icon icon, int color, float x, float y, float ax, float ay)
    {
        if (icon.texture == null)
        {
            return;
        }

        x -= icon.w * ax;
        y -= icon.h * ay;

        this.texturedBox(this.context.getTextures().getTexture(icon.texture), color, x, y, icon.w, icon.h, icon.x, icon.y, icon.x + icon.w, icon.y + icon.h, icon.textureW, icon.textureH);
    }

    public void iconArea(Icon icon, float x, float y, float w, float h)
    {
        this.iconArea(icon, Colors.WHITE, x, y, w, h);
    }

    public void iconArea(Icon icon, int color, float x, float y, float w, float h)
    {
        this.texturedArea(this.context.getTextures().getTexture(icon.texture), color, x, y, w, h, icon.x, icon.y, icon.w, icon.h, icon.textureW, icon.textureH);
    }

    public void outlinedIcon(Icon icon, float x, float y, float ax, float ay)
    {
        this.outlinedIcon(icon, x, y, Colors.WHITE, ax, ay);
    }

    /**
     * Draw an icon with a black outline.
     */
    public void outlinedIcon(Icon icon, float x, float y, int color, float ax, float ay)
    {
        this.icon(icon, Colors.A100, x - 1, y, ax, ay);
        this.icon(icon, Colors.A100, x + 1, y, ax, ay);
        this.icon(icon, Colors.A100, x, y - 1, ax, ay);
        this.icon(icon, Colors.A100, x, y + 1, ax, ay);
        this.icon(icon, color, x, y, ax, ay);
    }

    /* Textured box */

    public void fullTexturedBox(Texture texture, float x, float y, float w, float h)
    {
        this.fullTexturedBox(texture, x, y, w, h);
    }

    public void fullTexturedBox(Texture texture, int color, float x, float y, float w, float h)
    {
        this.texturedBox(texture, color, x, y, w, h, 0, 0, w, h, (int) w, (int) h);
    }

    public void fullTexturedBox(Shader shader, Texture texture, int color, float x, float y, float w, float h)
    {
        this.texturedBox(shader, texture, color, x, y, w, h, 0, 0, w, h, (int) w, (int) h);
    }

    public void texturedBox(Texture texture, int color, float x, float y, float w, float h, float u1, float v1, float u2, float v2)
    {
        this.texturedBox(texture, color, x, y, w, h, u1, v1, u2, v2, texture.width, texture.height);
    }

    public void texturedBox(Texture texture, int color, float x, float y, float w, float h, float u, float v)
    {
        this.texturedBox(texture, color, x, y, w, h, u, v, u + w, v + h, texture.width, texture.height);
    }

    public void texturedBox(Texture texture, int color, float x, float y, float w, float h, float u1, float v1, float u2, float v2, int textureW, int textureH)
    {
        this.begin(VBOAttributes.VERTEX_UV_RGBA_2D, texture);

        c1.set(color);

        this.fillTexturedBox(x, y, w, h, u1, v1, u2, v2, textureW, textureH);
    }

    private void fillTexturedBox(float x, float y, float w, float h, float u1, float v1, float u2, float v2, int textureW, int textureH)
    {
        /* 0, 1, 2, 0, 2, 3 */
        this.builder.xy(x, y + h).uv(u1, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x + w, y + h).uv(u2, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x + w, y).uv(u2, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x, y + h).uv(u1, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x + w, y).uv(u2, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        this.builder.xy(x, y).uv(u1, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
    }

    /* Textured box (with shader) */

    public void texturedBox(Shader shader, Texture texture, int color, float x, float y, float w, float h, float u1, float v1, float u2, float v2)
    {
        this.texturedBox(shader, texture, color, x, y, w, h, u1, v1, u2, v2, texture.width, texture.height);
    }

    public void texturedBox(Shader shader, Texture texture, int color, float x, float y, float w, float h, float u, float v)
    {
        this.texturedBox(shader, texture, color, x, y, w, h, u, v, u + w, v + h, texture.width, texture.height);
    }

    public void texturedBox(Shader shader, Texture texture, int color, float x, float y, float w, float h, float u1, float v1, float u2, float v2, int textureW, int textureH)
    {
        this.begin(shader, texture);

        c1.set(color);

        this.fillTexturedBox(x, y, w, h, u1, v1, u2, v2, textureW, textureH);
    }

    /* Repeatable textured box */

    public void texturedArea(Texture texture, int color, float x, float y, float w, float h, float u, float v, float tileW, float tileH, int tw, int th)
    {
        int countX = (int) (((w - 1) / tileW) + 1);
        int countY = (int) (((h - 1) / tileH) + 1);
        float fillerX = w - (countX - 1) * tileW;
        float fillerY = h - (countY - 1) * tileH;

        for (int i = 0, c = countX * countY; i < c; i ++)
        {
            float ix = i % countX;
            float iy = i / countX;
            float xx = x + ix * tileW;
            float yy = y + iy * tileH;
            float xw = ix == countX - 1 ? fillerX : tileW;
            float yh = iy == countY - 1 ? fillerY : tileH;

            this.texturedBox(texture, color, xx, yy, xw, yh, u, v, u + xw, v + yh, tw, th);
        }
    }

    /* Text with default font */

    public void text(String label, float x, float y, int color)
    {
        this.text(this.context.getFont(), label, x, y, color, false);
    }

    public void text(String label, float x, float y)
    {
        this.text(this.context.getFont(), label, x, y, Colors.WHITE, false);
    }

    public void textShadow(String label, float x, float y)
    {
        this.text(this.context.getFont(), label, x, y, Colors.WHITE, true);
    }

    public void textShadow(String label, float x, float y, int color)
    {
        this.text(this.context.getFont(), label, x, y, color, true);
    }

    public void text(String label, float x, float y, int color, boolean shadow)
    {
        this.text(this.context.getFont(), label, x, y, color, shadow);
    }

    /* Text */

    public void text(FontRenderer font, String label, float x, float y, int color)
    {
        this.text(font, label, x, y, color, false);
    }

    public void text(FontRenderer font, String label, float x, float y)
    {
        this.text(font, label, x, y, Colors.WHITE, false);
    }

    public void textShadow(FontRenderer font, String label, float x, float y)
    {
        this.text(font, label, x, y, Colors.WHITE, true);
    }

    public void textShadow(FontRenderer font, String label, float x, float y, int color)
    {
        this.text(font, label, x, y, color, true);
    }

    public void text(FontRenderer font, String label, float x, float y, int color, boolean shadow)
    {
        VAOBuilder builder = this.begin(VBOAttributes.VERTEX_UV_RGBA_2D, this.context.getTextures().getTexture(font.texture));

        font.build(builder, label, (int) x, (int) y, 0, color, shadow);
    }

    /* Text helpers */

    public int wallText(FontRenderer font, String text, int x, int y, int color, int width)
    {
        return this.wallText(font, text, x, y, color, width, 12);
    }

    public int wallText(FontRenderer font, String text, int x, int y, int color, int width, int lineHeight)
    {
        return this.wallText(font, text, x, y, color, width, lineHeight, 0F, 0F);
    }

    public int wallText(FontRenderer font, String text, int x, int y, int color, int width, int lineHeight, float ax, float ay)
    {
        List<String> list = font.split(text, width);
        int h = (lineHeight * (list.size() - 1)) + font.getHeight();

        y -= h * ay;

        for (String string : list)
        {
            this.text(font, string, (int) (x + (width - font.getWidth(string)) * ax), y, color, true);

            y += lineHeight;
        }

        return h;
    }

    public void textCard(FontRenderer font, String text, float x, float y)
    {
        this.textCard(font, text, x, y, Colors.WHITE, Colors.A50);
    }

    /**
     * In this context, text card is a text with some background behind it
     */
    public void textCard(FontRenderer font, String text, float x, float y, int color, int background)
    {
        this.textCard(font, text, x, y, color, background, 3);
    }

    public void textCard(FontRenderer font, String text, float x, float y, int color, int background, float offset)
    {
        this.textCard(font, text, x, y, color, background, offset, true);
    }

    public void textCard(FontRenderer font, String text, float x, float y, int color, int background, float offset, boolean shadow)
    {
        int a = background >> 24 & 0xff;

        if (a != 0)
        {
            this.box(x - offset, y - offset, x + font.getWidth(text) + offset - 1, y + font.getHeight() + offset, background);
        }

        this.text(font, text, x, y, color, shadow);
    }

    /* Pipeline */

    public VAOBuilder begin(VBOAttributes attributes)
    {
        return this.begin(GL11.GL_TRIANGLES, this.context.getShaders().get(attributes), null);
    }

    public VAOBuilder begin(VBOAttributes attributes, Texture texture)
    {
        return this.begin(GL11.GL_TRIANGLES, this.context.getShaders().get(attributes), texture);
    }

    public VAOBuilder begin(int mode, VBOAttributes attributes, Texture texture)
    {
        return this.begin(mode, this.context.getShaders().get(attributes), texture);
    }

    public VAOBuilder begin(Shader shader)
    {
        return this.begin(GL11.GL_TRIANGLES, shader, null);
    }

    public VAOBuilder begin(Shader shader, Texture texture)
    {
        return this.begin(GL11.GL_TRIANGLES, shader, texture);
    }

    public VAOBuilder begin(int mode, Shader shader, Texture texture)
    {
        if (this.shader == shader && this.texture == texture && this.mode == mode)
        {
            return this.builder;
        }

        this.flush();

        this.builder = this.context.getVAO().setup(shader).stack(this.context.stack);
        this.mode = mode;
        this.shader = shader;
        this.texture = texture;

        this.builder.begin();

        return this.builder;
    }

    public void flush()
    {
        if (this.shader == null)
        {
            return;
        }

        if (this.texture != null)
        {
            this.texture.bind();
        }

        this.builder.render(this.mode);

        this.reset();
    }

    public void reset()
    {
        this.mode = 0;
        this.builder = null;
        this.shader = null;
        this.texture = null;
    }
}