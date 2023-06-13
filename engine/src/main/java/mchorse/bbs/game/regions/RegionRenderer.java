package mchorse.bbs.game.regions;

import mchorse.bbs.game.regions.shapes.BoxShape;
import mchorse.bbs.game.regions.shapes.CylinderShape;
import mchorse.bbs.game.regions.shapes.Shape;
import mchorse.bbs.game.regions.shapes.SphereShape;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class RegionRenderer
{
    public static final float SEGMENTS = 16;

    private Matrix3d a1 = new Matrix3d();
    private Matrix3d a2 = new Matrix3d();
    private Matrix3d a3 = new Matrix3d();
    private Vector3d vec = new Vector3d();
    private Vector3f v1 = new Vector3f();
    private Vector3f v2 = new Vector3f();

    public RegionRenderer()
    {
        this.a1.rotateY(MathUtils.PI / 2);
        this.a2.rotateY(MathUtils.PI / 4);
        this.a3.rotateY(MathUtils.PI / -4);
    }

    public void render(RenderingContext context, Region region, Vector3d position)
    {
        for (Shape shape : region.shapes)
        {
            this.renderShape(context, shape, position, new Color(1F, 1F, 1F));
        }
    }

    public void renderShape(RenderingContext context, Shape shape, Vector3d position, Color color)
    {
        Vector3d diff = new Vector3d(shape.pos).add(position);

        if (shape instanceof BoxShape)
        {
            this.renderBoxShape(context, (BoxShape) shape, diff, color);
        }
        else if (shape instanceof CylinderShape)
        {
            this.renderCylinderShape(context, (CylinderShape) shape, diff, color);
        }
        else if (shape instanceof SphereShape)
        {
            this.renderSphereShape(context, (SphereShape) shape, diff, color);
        }
    }

    private void renderBoxShape(RenderingContext context, BoxShape shape, Vector3d diff, Color color)
    {
        Draw.renderBox(context,
            diff.x - shape.size.x, diff.y - shape.size.y, diff.z - shape.size.z,
            shape.size.x * 2, shape.size.y * 2, shape.size.z * 2,
            color.r, color.g, color.b
        );
    }

    private void renderCylinderShape(RenderingContext context, CylinderShape shape, Vector3d diff, Color color)
    {
        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);
        VAOBuilder builder = context.getVAO().setup(shader);
        final float thickness = 0.025F;

        builder.begin();

        for (int i = 0; i < SEGMENTS; i++)
        {
            double a1 = i / SEGMENTS * Math.PI * 2;
            double a2 = (i + 1) / SEGMENTS * Math.PI * 2;

            Draw.fillLine(builder, thickness,
                (float) (Math.cos(a1) * shape.horizontal), (float) shape.vertical, (float) (Math.sin(a1) * shape.horizontal),
                (float) (Math.cos(a2) * shape.horizontal), (float) shape.vertical, (float) (Math.sin(a2) * shape.horizontal),
                color.r, color.g, color.b, 1F
            );

            Draw.fillLine(builder, thickness,
                (float) (Math.cos(a1) * shape.horizontal), (float) shape.vertical, (float) (Math.sin(a1) * shape.horizontal),
                (float) (Math.cos(a1) * shape.horizontal), (float) -shape.vertical, (float) (Math.sin(a1) * shape.horizontal),
                color.r, color.g, color.b, 1F
            );

            if (i == SEGMENTS - 1)
            {
                Draw.fillLine(builder, thickness,
                    (float) (Math.cos(a2) * shape.horizontal), (float) (shape.vertical), (float) (Math.sin(a2) * shape.horizontal),
                    (float) (Math.cos(a2) * shape.horizontal), (float) (-shape.vertical), (float) (Math.sin(a2) * shape.horizontal),
                    color.r, color.g, color.b, 1F
                );
            }

            Draw.fillLine(builder, thickness,
                (float) (Math.cos(a1) * shape.horizontal), (float) (-shape.vertical), (float) (Math.sin(a1) * shape.horizontal),
                (float) (Math.cos(a2) * shape.horizontal), (float) (-shape.vertical), (float) (Math.sin(a2) * shape.horizontal),
                color.r, color.g, color.b, 1F
            );
        }

        context.stack.push();
        context.stack.translateRelative(context.getCamera(), diff);

        CommonShaderAccess.setModelView(shader, context.stack);

        context.stack.pop();

        builder.render(GL11.GL_TRIANGLES);
    }

    private void renderSphereShape(RenderingContext context, SphereShape shape, Vector3d diff, Color color)
    {
        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);
        VAOBuilder builder = context.getVAO().setup(shader);
        final float thickness = 0.025F;

        builder.begin();

        for (int i = 0; i < SEGMENTS; i++)
        {
            double a1 = i / SEGMENTS * Math.PI * 2;
            double a2 = (i + 1) / SEGMENTS * Math.PI * 2;

            Draw.fillLine(builder, thickness,
                (float) (Math.cos(a1) * shape.horizontal), 0, (float) (Math.sin(a1) * shape.horizontal),
                (float) (Math.cos(a2) * shape.horizontal), 0, (float) (Math.sin(a2) * shape.horizontal),
                color.r, color.g, color.b, 1F
            );

            Draw.fillLine(builder, thickness,
                (float) (Math.cos(a1) * shape.horizontal), (float) (Math.sin(a1) * shape.vertical), 0,
                (float) (Math.cos(a2) * shape.horizontal), (float) (Math.sin(a2) * shape.vertical), 0,
                color.r, color.g, color.b, 1F
            );

            /* Rotate 90 */
            Vector3d vector = this.rotate(this.a1, Math.cos(a1) * shape.horizontal, Math.sin(a1) * shape.vertical, 0);
            v1.set(vector);

            vector = this.rotate(this.a1, Math.cos(a2) * shape.horizontal, Math.sin(a2) * shape.vertical, 0);
            v2.set(vector);

            Draw.fillLine(builder, thickness,
                v1.x, v1.y, v1.z,
                v2.x, v2.y, v2.z,
                color.r, color.g, color.b, 1F
            );

            /* Rotate 45 */
            vector = this.rotate(this.a2, Math.cos(a1) * shape.horizontal, Math.sin(a1) * shape.vertical, 0);
            v1.set(vector);

            vector = this.rotate(this.a2, Math.cos(a2) * shape.horizontal, Math.sin(a2) * shape.vertical, 0);
            v2.set(vector);

            Draw.fillLine(builder, thickness,
                v1.x, v1.y, v1.z,
                v2.x, v2.y, v2.z,
                color.r, color.g, color.b, 1F
            );

            /* Rotate -45 */
            vector = this.rotate(this.a3, Math.cos(a1) * shape.horizontal, Math.sin(a1) * shape.vertical, 0);
            v1.set(vector);

            vector = this.rotate(this.a3, Math.cos(a2) * shape.horizontal, Math.sin(a2) * shape.vertical, 0);
            v2.set(vector);

            Draw.fillLine(builder, thickness,
                v1.x, v1.y, v1.z,
                v2.x, v2.y, v2.z,
                color.r, color.g, color.b, 1F
            );
        }

        context.stack.push();
        context.stack.translateRelative(context.getCamera(), diff);

        CommonShaderAccess.setModelView(shader, context.stack);

        context.stack.pop();

        builder.render(GL11.GL_TRIANGLES);
    }

    private Vector3d rotate(Matrix3d mat, double x, double y, double z)
    {
        this.vec.set(x, y, z);

        mat.transform(this.vec);

        return this.vec;
    }
}