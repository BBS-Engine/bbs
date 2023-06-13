package mchorse.bbs.graphics;

import mchorse.bbs.camera.Camera;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of a matrix stack
 */
public class MatrixStack
{
    public final List<Matrix4f> model = new ArrayList<Matrix4f>();
    public final List<Matrix3f> normal = new ArrayList<Matrix3f>();

    public final Matrix4f tempModelMatrix = new Matrix4f();
    public final Matrix3f tempNormalMatrix = new Matrix3f();

    private int depth = 1;

    public MatrixStack()
    {
        this.reset();
    }

    public void reset()
    {
        this.setDepth(1);

        this.identity();
    }

    private void setDepth(int depth)
    {
        this.depth = depth;

        while (this.model.size() < this.depth)
        {
            this.model.add(new Matrix4f());
            this.normal.add(new Matrix3f());
        }
    }

    public Matrix4f getModelMatrix()
    {
        return this.model.get(this.depth - 1);
    }

    public Matrix3f getNormalMatrix()
    {
        return this.normal.get(this.depth - 1);
    }

    public void push()
    {
        this.push(this.getModelMatrix(), this.getNormalMatrix());
    }

    public void push(Matrix4f model)
    {
        this.push(model, this.tempNormalMatrix.set(model));
    }

    public void push(Matrix4f model, Matrix3f normal)
    {
        this.setDepth(this.depth + 1);

        this.getModelMatrix().set(model);
        this.getNormalMatrix().set(normal);
    }

    public void pop()
    {
        if (this.depth <= 1)
        {
            throw new IllegalStateException("A one level stack can't be popped!");
        }

        this.setDepth(this.depth - 1);
    }

    public void identity()
    {
        this.getModelMatrix().identity();
        this.getNormalMatrix().identity();
    }

    public void multiply(Matrix4f matrix)
    {
        this.getModelMatrix().mul(matrix);
        this.getNormalMatrix().mul(this.tempNormalMatrix.set(matrix));
    }

    /* Translate */

    public void translate(Vector3f vector)
    {
        this.translate(vector.x, vector.y, vector.z);
    }

    public void translate(float x, float y, float z)
    {
        this.tempModelMatrix.identity();
        this.tempModelMatrix.setTranslation(x, y, z);

        this.getModelMatrix().mul(this.tempModelMatrix);
    }

    public void translateRelative(Camera camera, Vector3d vector)
    {
        this.translateRelative(camera, vector.x, vector.y, vector.z);
    }

    public void translateRelative(Camera camera, double x, double y, double z)
    {
        this.translate(camera.getRelative(x, y, z));
    }

    /* Scale */

    public void scale(float x, float y, float z)
    {
        this.getModelMatrix().scale(x, y, z);

        if (x < 0 || y < 0 || z < 0)
        {
            x = x < 0 ? -1 : 1;
            y = y < 0 ? -1 : 1;
            z = z < 0 ? -1 : 1;

            this.getNormalMatrix().scale(x, y, z);
        }
    }

    /* Rotate */

    public void rotateX(float radian)
    {
        this.tempModelMatrix.identity();
        this.tempModelMatrix.rotateX(radian);

        this.tempNormalMatrix.identity();
        this.tempNormalMatrix.rotateX(radian);

        this.getModelMatrix().mul(this.tempModelMatrix);
        this.getNormalMatrix().mul(this.tempNormalMatrix);
    }

    public void rotateY(float radian)
    {
        this.tempModelMatrix.identity();
        this.tempModelMatrix.rotateY(radian);

        this.tempNormalMatrix.identity();
        this.tempNormalMatrix.rotateY(radian);

        this.getModelMatrix().mul(this.tempModelMatrix);
        this.getNormalMatrix().mul(this.tempNormalMatrix);
    }

    public void rotateZ(float radian)
    {
        this.tempModelMatrix.identity();
        this.tempModelMatrix.rotateZ(radian);

        this.tempNormalMatrix.identity();
        this.tempNormalMatrix.rotateZ(radian);

        this.getModelMatrix().mul(this.tempModelMatrix);
        this.getNormalMatrix().mul(this.tempNormalMatrix);
    }
}
