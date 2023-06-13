package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Stack;

public class UIDraw
{
    private static final Color c1 = new Color();
    private static final Color c2 = new Color();
    private static final Color c3 = new Color();
    private static final Color c4 = new Color();

    public final RenderingContext context;

    private Stack<Area> scissors = new Stack<Area>();

    public UIDraw(RenderingContext context)
    {
        this.context = context;
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

    public void normalizedBox(int x1, int y1, int x2, int y2, int color)
    {
        int temp = x1;

        x1 = Math.min(x1, x2);
        x2 = Math.max(temp, x2);

        temp = y1;

        y1 = Math.min(y1, y2);
        y2 = Math.max(temp, y2);

        this.box(x1, y1, x2, y2, color);
    }

    public void box(int x1, int y1, int x2, int y2, int color)
    {
        this.box(x1, y1, x2, y2, color, color, color, color);
    }

    public void box(int x1, int y1, int x2, int y2, int color1, int color2, int color3, int color4)
    {
        VAOBuilder builder = this.context.getVAO().setup(this.context.getShaders().get(VBOAttributes.VERTEX_RGBA_2D));

        builder.begin();
        this.fillBox(builder, x1, y1, x2, y2, color1, color2, color3, color4);
        builder.render();
    }

    public void fillBox(VAOBuilder builder, int x1, int y1, int x2, int y2, int color1, int color2, int color3, int color4)
    {
        c1.set(color1);
        c2.set(color2);
        c3.set(color3);
        c4.set(color4);

        /* c1 ---- c2
         * |        |
         * c3 ---- c4 */
        builder.xy(x1, y1).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(x1, y2).rgba(c3.r, c3.g, c3.b, c3.a);
        builder.xy(x2, y2).rgba(c4.r, c4.g, c4.b, c4.a);
        builder.xy(x1, y1).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(x2, y2).rgba(c4.r, c4.g, c4.b, c4.a);
        builder.xy(x2, y1).rgba(c2.r, c2.g, c2.b, c2.a);
    }

    /* Gradients */

    public void gradientHBox(int x1, int y1, int x2, int y2, int leftColor, int rightColor)
    {
        this.box(x1, y1, x2, y2, leftColor, rightColor, leftColor, rightColor);
    }

    public void gradientVBox(int x1, int y1, int x2, int y2, int topColor, int bottomColor)
    {
        this.box(x1, y1, x2, y2, topColor, topColor, bottomColor, bottomColor);
    }

    /* Textured box */

    public void fullTexturedBox(int x, int y, int w, int h)
    {
        this.scaledTexturedBox(Colors.WHITE, x, y, 0, 0, w, h, w, h);
    }

    public void scaledTexturedBox(int x, int y, float u, float v, int w, int h, int textureW, int textureH)
    {
        this.scaledTexturedBox(Colors.WHITE, x, y, u, v, w, h, textureW, textureH);
    }

    public void scaledTexturedBox(int color, int x, int y, float u, float v, int w, int h, int textureW, int textureH)
    {
        this.scaledTexturedBox(this.context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D), color, x, y, u, v, w, h, textureW, textureH);
    }

    /**
     * Draw a textured quad with given UV, dimensions and custom texture size
     */
    public void scaledTexturedBox(Shader shader, int color, int x, int y, float u, float v, int w, int h, int textureW, int textureH)
    {
        VAOBuilder builder = this.context.getVAO().setup(shader);

        builder.begin();
        this.fillTexturedBox(builder, color, x, y, u, v, w, h, textureW, textureH, u + w, v + h);
        builder.render();
    }

    public void customTexturedBox(int x, int y, float u1, float v1, int w, int h, int textureW, int textureH, float u2, float v2)
    {
        this.customTextured(this.context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D), Colors.WHITE, x, y, u1, v1, w, h, textureW, textureH, u2, v2);
    }

    public void customTexturedBox(int color, int x, int y, float u1, float v1, int w, int h, int textureW, int textureH, float u2, float v2)
    {
        this.customTextured(this.context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D), color, x, y, u1, v1, w, h, textureW, textureH, u2, v2);
    }

    /**
     * Draw a textured quad with given UV, dimensions and custom texture size
     */
    public void customTextured(Shader shader, int color, int x, int y, float u1, float v1, int w, int h, int textureW, int textureH, float u2, float v2)
    {
        VAOBuilder builder = this.context.getVAO().setup(shader);

        builder.begin();
        this.fillTexturedBox(builder, color, x, y, u1, v1, w, h, textureW, textureH, u2, v2);
        builder.render();
    }

    public void fillTexturedBox(VAOBuilder builder, int color, int x, int y, float u1, float v1, int w, int h, int textureW, int textureH, float u2, float v2)
    {
        c1.set(color);

        /* 0, 1, 2, 0, 2, 3 */
        builder.xy(x, y + h).uv(u1, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(x + w, y + h).uv(u2, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(x + w, y).uv(u2, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(x, y + h).uv(u1, v2, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(x + w, y).uv(u2, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(x, y).uv(u1, v1, textureW, textureH).rgba(c1.r, c1.g, c1.b, c1.a);
    }

    public void texturedArea(int color, int x, int y, int w, int h, float u, float v, int tileW, int tileH, int tw, int th)
    {
        VAOBuilder builder = this.context.getVAO().setup(this.context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D));

        builder.begin();

        this.fillTexturedArea(builder, color, x, y, w, h, u, v, tileW, tileH, tw, th);

        builder.render();
    }

    public void fillTexturedArea(VAOBuilder builder, int color, int x, int y, int w, int h, float u, float v, int tileW, int tileH, int tw, int th)
    {
        int countX = ((w - 1) / tileW) + 1;
        int countY = ((h - 1) / tileH) + 1;
        int fillerX = w - (countX - 1) * tileW;
        int fillerY = h - (countY - 1) * tileH;

        for (int i = 0, c = countX * countY; i < c; i ++)
        {
            int ix = i % countX;
            int iy = i / countX;
            int xx = x + ix * tileW;
            int yy = y + iy * tileH;
            int xw = ix == countX - 1 ? fillerX : tileW;
            int yh = iy == countY - 1 ? fillerY : tileH;

            this.fillTexturedBox(builder, color, xx, yy, u, v, xw, yh, tw, th, u + xw, v + yh);
        }
    }

    /* Outline methods */

    public void outlineCenter(int x, int y, int offset, int color)
    {
        this.outlineCenter(x, y, offset, color, 1);
    }

    public void outlineCenter(int x, int y, int offset, int color, int border)
    {
        this.outline(x - offset, y - offset, x + offset, y + offset, color, border);
    }

    public void outline(int left, int top, int right, int bottom, int color)
    {
        this.outline(left, top, right, bottom, color, 1);
    }

    /**
     * Draw rectangle outline with given border.
     */
    public void outline(int left, int top, int right, int bottom, int c, int border)
    {
        VAOBuilder builder = this.context.getVAO().setup(this.context.getShaders().get(VBOAttributes.VERTEX_RGBA_2D));

        builder.begin();
        this.fillBox(builder, left, top, left + border, bottom, c, c, c, c);
        this.fillBox(builder, right - border, top, right, bottom, c, c, c, c);
        this.fillBox(builder, left + border, top, right - border, top + border, c, c, c, c);
        this.fillBox(builder, left + border, bottom - border, right - border, bottom, c, c, c, c);
        builder.render();
    }

    public void outlinedIcon(Icon icon, int x, int y, float ax, float ay)
    {
        outlinedIcon(icon, x, y, Colors.WHITE, ax, ay);
    }

    /**
     * Draw an icon with a black outline.
     */
    public void outlinedIcon(Icon icon, int x, int y, int color, float ax, float ay)
    {
        VAOBuilder builder = this.context.getVAO().setup(this.context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D));

        icon.bindTexture(this);

        builder.begin();
        icon.fill(this, builder, x - 1, y, Colors.A100, ax, ay);
        icon.fill(this, builder, x + 1, y, Colors.A100, ax, ay);
        icon.fill(this, builder, x, y - 1, Colors.A100, ax, ay);
        icon.fill(this, builder, x, y + 1, Colors.A100, ax, ay);
        icon.fill(this, builder, x, y, color, ax, ay);
        builder.render();
    }

    /* Shadows */

    public void dropShadow(int left, int top, int right, int bottom, int offset, int opaque, int shadow)
    {
        left -= offset;
        top -= offset;
        right += offset;
        bottom += offset;

        c1.set(opaque);
        c2.set(shadow);

        VAOBuilder builder = this.context.getVAO().setup(this.context.getShaders().get(VBOAttributes.VERTEX_RGBA_2D));

        builder.begin();

        /* Draw opaque part */
        builder.xy(right - offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(left + offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(right - offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(left + offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);

        /* Draw top shadow */
        builder.xy(right, top).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(left, top).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(right, top).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(right - offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);

        /* Draw bottom shadow */
        builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(left + offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(left, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(left, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(right, bottom).rgba(c2.r, c2.g, c2.b, c2.a);

        /* Draw left shadow */
        builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(left, top).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(left, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(left + offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(left, bottom).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(left + offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);

        /* Draw right shadow */
        builder.xy(right, top).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(right - offset, top + offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(right, top).rgba(c2.r, c2.g, c2.b, c2.a);
        builder.xy(right - offset, bottom - offset).rgba(c1.r, c1.g, c1.b, c1.a);
        builder.xy(right, bottom).rgba(c2.r, c2.g, c2.b, c2.a);

        builder.render();
    }

    public void dropCircleShadow(int x, int y, int radius, int segments, int opaque, int shadow)
    {
        c1.set(opaque);
        c2.set(shadow);

        VAOBuilder builder = this.context.getVAO().setup(this.context.getShaders().get(VBOAttributes.VERTEX_RGBA_2D));

        builder.begin();
        builder.xy(x, y).rgba(c1.r, c1.g, c1.b, c1.a);

        for (int i = 0; i <= segments; i ++)
        {
            double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

            builder.xy((float) (x - Math.cos(a) * radius), (float) (y + Math.sin(a) * radius)).rgba(c2.r, c2.g, c2.b, c2.a);
        }

        builder.render(GL11.GL_TRIANGLE_FAN);
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

        VAOBuilder builder = this.context.getVAO().setup(this.context.getShaders().get(VBOAttributes.VERTEX_RGBA_2D));

        /* Draw opaque base */
        builder.begin();
        builder.xy(x, y).rgba(c1.r, c1.g, c1.b, c1.a);

        for (int i = 0; i <= segments; i ++)
        {
            double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

            builder.xy((int) (x - Math.cos(a) * offset), (int) (y + Math.sin(a) * offset)).rgba(c1.r, c1.g, c1.b, c1.a);
        }

        builder.render(GL11.GL_TRIANGLE_FAN);

        /* Draw outer shadow */
        builder.begin();

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

        builder.render();
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
            font.renderWithShadow(context, string, (int) (x + (width - font.getWidth(string)) * ax), y, color);

            y += lineHeight;
        }

        return h;
    }

    public void textCard(FontRenderer font, String text, int x, int y)
    {
        this.textCard(font, text, x, y, Colors.WHITE, Colors.A50);
    }

    /**
     * In this context, text card is a text with some background behind it
     */
    public void textCard(FontRenderer font, String text, int x, int y, int color, int background)
    {
        this.textCard(font, text, x, y, color, background, 3);
    }

    public void textCard(FontRenderer font, String text, int x, int y, int color, int background, int offset)
    {
        this.textCard(font, text, x, y, color, background, offset, true);
    }

    public void textCard(FontRenderer font, String text, int x, int y, int color, int background, int offset, boolean shadow)
    {
        int a = background >> 24 & 0xff;

        if (a != 0)
        {
            this.box(x - offset, y - offset, x + font.getWidth(text) + offset - 1, y + font.getHeight() + offset, background);
        }

        font.render(this.context, text, x, y, color, shadow);
    }

    /* UI helpers */

    /**
     * Generic method for rendering locked (disabled) state of an input field
     */
    public void lockedArea(UIElement element)
    {
        if (!element.isEnabled())
        {
            element.area.render(this, Colors.A50);

            this.outlinedIcon(Icons.LOCKED, element.area.mx(), element.area.my(), 0.5F, 0.5F);
        }
    }

    public void background(int x, int y, int width, int height)
    {
        Link background = BBSSettings.backgroundImage.get();
        int color = BBSSettings.backgroundColor.get();

        if (background == null)
        {
            this.box(x, y, x + width, y + height, color);
        }
        else
        {
            this.context.getTextures().bind(background);

            this.scaledTexturedBox(color, x, y, 0, 0, width, height, width, height);
        }
    }
}