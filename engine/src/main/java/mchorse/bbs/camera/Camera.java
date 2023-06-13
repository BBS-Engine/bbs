package mchorse.bbs.camera;

import mchorse.bbs.BBS;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Matrix4f;
import org.joml.RayAabIntersection;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera
{
    public Matrix4f projection = new Matrix4f();
    public Matrix4f view = new Matrix4f();
    public float fov;
    public float near = 0.005F;
    public float far = 300F;

    public Vector3d position = new Vector3d();
    public Vector3f rotation = new Vector3f();

    private Vector3f relative = new Vector3f();

    public Camera()
    {
        this.setFov(70);
    }

    public void setFov(float degrees)
    {
        this.fov = MathUtils.toRad(degrees);
    }

    public void setFarNear(float near, float far)
    {
        this.near = near;
        this.far = far;
    }

    public RayAabIntersection getMouseIntersection(int mx, int my)
    {
        Vector3f direction = this.getMouseDirection(mx, my);

        return new RayAabIntersection((float) this.position.x, (float) this.position.y, (float) this.position.z, direction.x, direction.y, direction.z);
    }

    public Vector3f getLookDirection()
    {
        return Matrices.rotation(this.rotation.x, MathUtils.PI - this.rotation.y);
    }

    public Vector3f getMouseDirection()
    {
        MouseInput mouse = BBS.getEngine().mouse;

        return this.getMouseDirection(mouse.x, mouse.y);
    }

    public Vector3f getMouseDirection(int mx, int my)
    {
        return this.getMouseDirection(mx, my, Window.width, Window.height);
    }

    public Vector3f getMouseDirection(int mx, int my, Area viewport)
    {
        return this.getMouseDirection(mx - viewport.x, my - viewport.y, viewport.w, viewport.h);
    }

    public Vector3f getMouseDirection(int mx, int my, int w, int h)
    {
        return this.getMouseDirection(mx / (float) w, 1 - my / (float) h);
    }

    public Vector3f getMouseDirection(float mx, float my)
    {
        return this.getMouseDirectionNormalized((mx - 0.5F) * 2F, (my - 0.5F) * 2F);
    }

    public Vector3f getMouseDirectionNormalized(float mx, float my)
    {
        Matrix4f matrix4f = new Matrix4f(this.projection);

        matrix4f.mul(this.view);
        matrix4f.invert();

        Vector4f forward = new Vector4f(mx, my, 0, 1);

        matrix4f.transform(forward);

        return new Vector3f(forward.x, forward.y, forward.z);
    }

    public Vector3f getRelative(Vector3d vector)
    {
        return this.getRelative(vector.x, vector.y, vector.z);
    }

    public Vector3f getRelative(double x, double y, double z)
    {
        return this.relative.set((float) (x - this.position.x), (float) (y - this.position.y), (float) (z - this.position.z));
    }

    public void updatePerspectiveProjection(int width, int height)
    {
        this.projection.identity().perspective(this.fov, width / (float) height, this.near, this.far);
    }

    public void updateOrthoProjection(int width, int height)
    {
        this.projection.identity().ortho(-width, width, -height, height, this.near, this.far);
    }

    public Matrix4f updateView()
    {
        return this.view.identity()
            .rotateZ(this.rotation.z)
            .rotateX(this.rotation.x)
            .rotateY(this.rotation.y);
    }

    public void copy(Camera camera)
    {
        this.projection.set(camera.projection);
        this.view.set(camera.view);
        this.fov = camera.fov;
        this.near = camera.near;
        this.far = camera.far;
        this.position.set(camera.position);
        this.rotation.set(camera.rotation);
    }
}