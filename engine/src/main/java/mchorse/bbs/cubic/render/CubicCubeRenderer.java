package mchorse.bbs.cubic.render;

import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelCube;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.cubic.data.model.ModelMesh;
import mchorse.bbs.cubic.data.model.ModelQuad;
import mchorse.bbs.cubic.data.model.ModelVertex;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CubicCubeRenderer implements ICubicRenderer
{
    protected float r = 1;
    protected float g = 1;
    protected float b = 1;
    protected float a = 1;

    /* Temporary variables to avoid allocating and GC vectors */
    protected Vector3f normal = new Vector3f();
    protected Vector4f vertex = new Vector4f();

    private ModelVertex modelVertex = new ModelVertex();

    public static void moveToPivot(MatrixStack stack, Vector3f pivot)
    {
        stack.translate(pivot.x / 16F, pivot.y / 16F, pivot.z / 16F);
    }

    public static void rotate(MatrixStack stack, Vector3f rotation)
    {
        if (rotation.x == 0 && rotation.y == 0 && rotation.z == 0)
        {
            return;
        }

        Matrix4f matrix4f = new Matrix4f();
        Matrix3f matrix3f = new Matrix3f();

        stack.tempModelMatrix.identity();
        matrix4f.identity().rotateZ(MathUtils.toRad(rotation.z));
        stack.tempModelMatrix.mul(matrix4f);

        matrix4f.identity().rotateY(MathUtils.toRad(rotation.y));
        stack.tempModelMatrix.mul(matrix4f);

        matrix4f.identity().rotateX(MathUtils.toRad(rotation.x));
        stack.tempModelMatrix.mul(matrix4f);

        stack.tempNormalMatrix.identity();
        matrix3f.identity().rotateZ(MathUtils.toRad(rotation.z));
        stack.tempNormalMatrix.mul(matrix3f);

        matrix3f.identity().rotateY(MathUtils.toRad(rotation.y));
        stack.tempNormalMatrix.mul(matrix3f);

        matrix3f.identity().rotateX(MathUtils.toRad(rotation.x));
        stack.tempNormalMatrix.mul(matrix3f);

        stack.getModelMatrix().mul(stack.tempModelMatrix);
        stack.getNormalMatrix().mul(stack.tempNormalMatrix);
    }

    public static void moveBackFromPivot(MatrixStack stack, Vector3f pivot)
    {
        stack.translate(-pivot.x / 16F, -pivot.y / 16F, -pivot.z / 16F);
    }

    public void setColor(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public boolean renderGroup(VAOBuilder builder, MatrixStack stack, ModelGroup group, Model model)
    {
        for (ModelCube cube : group.cubes)
        {
            this.renderCube(builder, stack, group, cube);
        }

        for (ModelMesh mesh : group.meshes)
        {
            this.renderMesh(builder, stack, model, group, mesh);
        }

        return false;
    }

    private void renderCube(VAOBuilder builder, MatrixStack stack, ModelGroup group, ModelCube cube)
    {
        stack.push();
        moveToPivot(stack, cube.pivot);
        rotate(stack, cube.rotate);
        moveBackFromPivot(stack, cube.pivot);

        for (ModelQuad quad : cube.quads)
        {
            this.normal.set(quad.normal.x, quad.normal.y, quad.normal.z);
            stack.getNormalMatrix().transform(this.normal);

            /* For 0 sized cubes on either axis, to avoid getting dark shading on models
             * which didn't correctly setup the UV faces.
             *
             * For example two wings, first wing uses top face for texturing the flap,
             * and second wing uses bottom face as a flap. In the end, the second wing
             * will appear dark shaded without this fix.
             */
            if (this.normal.x < 0 && (cube.size.y == 0 || cube.size.z == 0)) this.normal.x *= -1;
            if (this.normal.y < 0 && (cube.size.x == 0 || cube.size.z == 0)) this.normal.y *= -1;
            if (this.normal.z < 0 && (cube.size.x == 0 || cube.size.y == 0)) this.normal.z *= -1;

            if (quad.vertices.size() == 4)
            {
                this.writeVertex(builder, stack, group, quad.vertices.get(0));
                this.writeVertex(builder, stack, group, quad.vertices.get(1));
                this.writeVertex(builder, stack, group, quad.vertices.get(2));
                this.writeVertex(builder, stack, group, quad.vertices.get(0));
                this.writeVertex(builder, stack, group, quad.vertices.get(2));
                this.writeVertex(builder, stack, group, quad.vertices.get(3));
            }
        }

        stack.pop();
    }

    private void renderMesh(VAOBuilder builder, MatrixStack stack, Model model, ModelGroup group, ModelMesh mesh)
    {
        stack.push();
        moveToPivot(stack, mesh.origin);
        rotate(stack, mesh.rotate);
        moveBackFromPivot(stack, mesh.origin);

        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();

        for (int i = 0, c = mesh.vertices.size() / 3; i < c; i++)
        {
            Vector3f p1 = mesh.vertices.get(i * 3);
            Vector3f p2 = mesh.vertices.get(i * 3 + 1);
            Vector3f p3 = mesh.vertices.get(i * 3 + 2);

            Vector2f uv1 = mesh.uvs.get(i * 3);
            Vector2f uv2 = mesh.uvs.get(i * 3 + 1);
            Vector2f uv3 = mesh.uvs.get(i * 3 + 2);

            /* Calculate normal */
            Vector3f normal = new Vector3f();

            a.set(p2).sub(p1);
            b.set(p3).sub(p1);

            a.cross(b, normal);
            normal.normalize();

            this.normal.set(normal.x, normal.y, normal.z);
            stack.getNormalMatrix().transform(this.normal);

            /* Write vertices */
            this.modelVertex.set(p1, uv1, model);
            this.writeVertex(builder, stack, group, this.modelVertex);

            this.modelVertex.set(p2, uv2, model);
            this.writeVertex(builder, stack, group, this.modelVertex);

            this.modelVertex.set(p3, uv3, model);
            this.writeVertex(builder, stack, group, this.modelVertex);
        }

        stack.pop();
    }

    protected void writeVertex(VAOBuilder builder, MatrixStack stack, ModelGroup group, ModelVertex vertex)
    {
        this.vertex.set(vertex.vertex.x, vertex.vertex.y, vertex.vertex.z, 1);
        stack.getModelMatrix().transform(this.vertex);

        builder.xyz(this.vertex.x, this.vertex.y, this.vertex.z)
            .normal(this.normal.x, this.normal.y, this.normal.z)
            .uv(vertex.uv.x, vertex.uv.y)
            .rgba(this.r, this.g, this.b, this.a);
    }
}