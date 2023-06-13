package mchorse.bbs.camera.controller;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.OrbitCamera;
import mchorse.bbs.utils.math.MathUtils;

public class OrbitCameraController implements ICameraController
{
    public final OrbitCamera camera;

    private final int priority;

    public OrbitCameraController(OrbitCamera camera)
    {
        this(camera, 100);
    }

    public OrbitCameraController(OrbitCamera camera, int priority)
    {
        this.camera = camera;
        this.priority = priority;
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        camera.position.set(this.camera.getFinalPosition());
        camera.rotation.set(this.camera.rotation);
        camera.fov = BBSSettings.getFov();
    }

    @Override
    public int getPriority()
    {
        return this.priority;
    }
}