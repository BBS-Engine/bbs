package mchorse.bbs.graphics;

import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.Quad;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.IBlockAccessor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class Draw
{
    private static final Quad top = new Quad();
    private static final Quad bottom = new Quad();
    private static final Matrix4f rotate = new Matrix4f();

    public static void renderBox(RenderingContext context, double x, double y, double z, double w, double h, double d)
    {
        renderBox(context, x, y, z, w, h, d, 1, 1, 1);
    }

    public static void renderBlockAABB(RenderingContext context, IBlockAccessor blockAccessor, int x, int y, int z)
    {
        AABB box = blockAccessor.getBlock(x, y, z).getModel().collisionBox;

        Draw.renderBox(context, x + box.x, y + box.y, z + box.z, box.w, box.h, box.d);
    }

    public static void renderBox(RenderingContext context, double x, double y, double z, double w, double h, double d, float r, float g, float b)
    {
        renderBox(context, x, y, z, w, h, d, r, g, b, 1F);
    }

    public static void renderBox(RenderingContext context, double x, double y, double z, double w, double h, double d, float r, float g, float b, float a)
    {
        context.stack.push();
        context.stack.identity();
        context.stack.translateRelative(context.getCamera(), x, y, z);
        float fw = (float) w;
        float fh = (float) h;
        float fd = (float) d;
        float t = 1 / 96F + (float) (Math.sqrt(w * w + h + h + d + d) / 2000);

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);

        CommonShaderAccess.setModelView(shader, context.stack);

        VAOBuilder builder = context.getVAO().setup(shader);

        builder.begin();

        /* Pillars: fillBox(builder, -t, -t, -t, t, t, t, r, g, b, a); */
        fillBox(builder, -t, -t, -t, t, t + fh, t, r, g, b, a);
        fillBox(builder, -t + fw, -t, -t, t + fw, t + fh, t, r, g, b, a);
        fillBox(builder, -t, -t, -t + fd, t, t + fh, t + fd, r, g, b, a);
        fillBox(builder, -t + fw, -t, -t + fd, t + fw, t + fh, t + fd, r, g, b, a);

        /* Top */
        fillBox(builder, -t, -t + fh, -t, t + fw, t + fh, t, r, g, b, a);
        fillBox(builder, -t, -t + fh, -t + fd, t + fw, t + fh, t + fd, r, g, b, a);
        fillBox(builder, -t, -t + fh, -t, t, t + fh, t + fd, r, g, b, a);
        fillBox(builder, -t + fw, -t + fh, -t, t + fw, t + fh, t + fd, r, g, b, a);

        /* Bottom */
        fillBox(builder, -t, -t, -t, t + fw, t, t, r, g, b, a);
        fillBox(builder, -t, -t, -t + fd, t + fw, t, t + fd, r, g, b, a);
        fillBox(builder, -t, -t, -t, t, t, t + fd, r, g, b, a);
        fillBox(builder, -t + fw, -t, -t, t + fw, t, t + fd, r, g, b, a);

        builder.render(GL11.GL_TRIANGLES);

        context.stack.pop();
    }

    /**
     * Fill a quad for {@link VBOAttributes#VERTEX_NORMAL_UV_RGBA}. Points should
     * be supplied in this order:
     *
     *     3 -------> 4
     *     ^
     *     |
     *     |
     *     2 <------- 1
     *
     * I.e. bottom left, bottom right, top left, top right, where left is -X and right is +X,
     * in case of a quad on fixed on Z axis.
     */
    public static void fillTexturedNormalQuad(VAOBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, float r, float g, float b, float a, float nx, float ny, float nz)
    {
        /* 1 - BL, 2 - BR, 3 - TR, 4 - TL */
        builder.xyz(x2, y2, z2).normal(nx, ny, nz).uv(u1, v2).rgba(r, g, b, a);
        builder.xyz(x1, y1, z1).normal(nx, ny, nz).uv(u2, v2).rgba(r, g, b, a);
        builder.xyz(x4, y4, z4).normal(nx, ny, nz).uv(u2, v1).rgba(r, g, b, a);

        builder.xyz(x2, y2, z2).normal(nx, ny, nz).uv(u1, v2).rgba(r, g, b, a);
        builder.xyz(x4, y4, z4).normal(nx, ny, nz).uv(u2, v1).rgba(r, g, b, a);
        builder.xyz(x3, y3, z3).normal(nx, ny, nz).uv(u1, v1).rgba(r, g, b, a);
    }

    /**
     * Fill a quad for {@link VBOAttributes#VERTEX_UV_RGBA}. Points should
     * be supplied in this order:
     *
     *     3 -------> 4
     *     ^
     *     |
     *     |
     *     2 <------- 1
     *
     * I.e. bottom left, bottom right, top left, top right, where left is -X and right is +X,
     * in case of a quad on fixed on Z axis.
     */
    public static void fillTexturedQuad(VAOBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, float r, float g, float b, float a)
    {
        /* 1 - BL, 2 - BR, 3 - TR, 4 - TL */
        builder.xyz(x2, y2, z2).uv(u1, v2).rgba(r, g, b, a);
        builder.xyz(x1, y1, z1).uv(u2, v2).rgba(r, g, b, a);
        builder.xyz(x4, y4, z4).uv(u2, v1).rgba(r, g, b, a);

        builder.xyz(x2, y2, z2).uv(u1, v2).rgba(r, g, b, a);
        builder.xyz(x4, y4, z4).uv(u2, v1).rgba(r, g, b, a);
        builder.xyz(x3, y3, z3).uv(u1, v1).rgba(r, g, b, a);
    }

    public static void fillQuad(VAOBuilder builder, Quad quad, float r, float g, float b, float a)
    {
        fillQuad(builder, quad.p1, quad.p2, quad.p3, quad.p4, r, g, b, a);
    }

    public static void fillQuad(VAOBuilder builder, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float r, float g, float b, float a)
    {
        fillQuad(builder, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z, p3.x, p3.y, p3.z, p4.x, p4.y, p4.z, r, g, b, a);
    }

    public static void fillQuad(VAOBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float r, float g, float b, float a)
    {
        /* 1 - BR, 2 - BL, 3 - TL, 4 - TR */
        builder.xyz(x1, y1, z1).rgba(r, g, b, a);
        builder.xyz(x2, y2, z2).rgba(r, g, b, a);
        builder.xyz(x3, y3, z3).rgba(r, g, b, a);
        builder.xyz(x1, y1, z1).rgba(r, g, b, a);
        builder.xyz(x3, y3, z3).rgba(r, g, b, a);
        builder.xyz(x4, y4, z4).rgba(r, g, b, a);
    }

    public static void fillBox(VAOBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b)
    {
        fillBox(builder, x1, y1, z1, x2, y2, z2, r, g, b, 1F);
    }

    public static void fillBox(VAOBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a)
    {
        /* X */
        fillQuad(builder, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1, r, g, b, a);
        fillQuad(builder, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, r, g, b, a);

        /* Y */
        fillQuad(builder, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, r, g, b, a);
        fillQuad(builder, x2, y2, z1, x1, y2, z1, x1, y2, z2, x2, y2, z2, r, g, b, a);

        /* Z */
        fillQuad(builder, x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1, r, g, b, a);
        fillQuad(builder, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, r, g, b, a);
    }

    public static void fillLine(VAOBuilder builder, float thickness, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a)
    {
        float length = new Vector3f(x2, y2, z2).sub(x1, y1, z1).length();
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float yaw = (float) -Math.atan2(dz, dx);
        float pitch = (float) Math.atan2(dy, Math.sqrt(dx * dx + dz * dz));

        thickness /= 2;

        bottom.p1.set(-thickness, 0, -thickness);
        bottom.p2.set(thickness, 0, -thickness);
        bottom.p3.set(thickness, 0, thickness);
        bottom.p4.set(-thickness, 0, thickness);
        top.p1.set(-thickness, length, -thickness);
        top.p2.set(thickness, length, -thickness);
        top.p3.set(thickness, length, thickness);
        top.p4.set(-thickness, length, thickness);

        rotate.identity()
            .translate(x1, y1, z1)
            .rotateY(yaw - MathUtils.PI / 2)
            .rotateX(pitch - MathUtils.PI / 2);

        bottom.transform(rotate);
        top.transform(rotate);

        /* X */
        fillQuad(builder, bottom.p4, top.p4, top.p1, bottom.p1, r, g, b, a);
        fillQuad(builder, bottom.p2, top.p2, top.p3, bottom.p3, r, g, b, a);

        /* Y */
        fillQuad(builder, bottom.p1, bottom.p2, bottom.p3, bottom.p4, r, g, b, a);
        fillQuad(builder, top.p2, top.p1, top.p4, top.p3, r, g, b, a);

        /* Z */
        fillQuad(builder, bottom.p2, bottom.p1, top.p1, top.p2, r, g, b, a);
        fillQuad(builder, bottom.p4, bottom.p3, top.p3, top.p4, r, g, b, a);
    }

    public static void axis(VAOBuilder builder, float length, float thickness)
    {
        fillBox(builder, thickness, -thickness, -thickness, length, thickness, thickness, 1, 0, 0, 1);
        fillBox(builder, -thickness, -thickness, -thickness, thickness, length, thickness, 0, 1, 0, 1);
        fillBox(builder, -thickness, -thickness, thickness, thickness, thickness, length, 0, 0, 1, 1);
    }
}