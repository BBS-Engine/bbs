package mchorse.bbs.camera.controller;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ProxyCameraController implements ICameraController
{
    public final Vector3d position = new Vector3d();
    public final Vector3f rotation = new Vector3f();
    public float fov = MathUtils.toRad(40);
    private int priority;

    public ProxyCameraController(int priority)
    {
        this.priority = priority;
    }

    public void update(Vector3d position, Vector3f rotation)
    {
        this.position.set(position);
        this.rotation.set(rotation);
    }

    public void update(Vector3d position, Vector3f rotation, float fov)
    {
        this.position.set(position);
        this.rotation.set(rotation);
        this.fov = fov;
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        camera.position.set(this.position);
        camera.rotation.set(this.rotation);
        camera.fov = this.fov;
    }

    @Override
    public int getPriority()
    {
        return this.priority;
    }
}