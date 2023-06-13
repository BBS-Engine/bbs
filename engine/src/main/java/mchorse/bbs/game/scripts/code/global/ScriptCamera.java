package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.controller.CameraController;
import mchorse.bbs.camera.controller.ICameraController;
import mchorse.bbs.camera.controller.PlayCameraController;
import mchorse.bbs.camera.controller.ProxyCameraController;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.user.global.IScriptCamera;
import mchorse.bbs.utils.math.MathUtils;

public class ScriptCamera implements IScriptCamera
{
    private ScriptBBS factory;

    public ScriptCamera(ScriptBBS factory)
    {
        this.factory = factory;
    }

    @Override
    public boolean play(String profile, boolean force)
    {
        this.unlock();

        CameraWork work = BBSData.getCameras().load(profile);

        if (work != null)
        {
            CameraController controller = this.factory.getBridge().get(IBridgeCamera.class).getCameraController();
            boolean playing = this.isPlaying(controller);

            if (!force && playing)
            {
                return false;
            }

            if (force && playing)
            {
                controller.remove(PlayCameraController.class);
            }

            controller.add(new PlayCameraController(this.factory.getBridge(), work));

            return true;
        }

        return false;
    }

    @Override
    public boolean isPlaying()
    {
        return this.isPlaying(this.factory.getBridge().get(IBridgeCamera.class).getCameraController());
    }

    private boolean isPlaying(CameraController controller)
    {
        return controller.getCurrent() instanceof PlayCameraController;
    }

    @Override
    public void stop()
    {
        CameraController controller = this.factory.getBridge().get(IBridgeCamera.class).getCameraController();

        if (this.isPlaying(controller))
        {
            controller.remove(PlayCameraController.class);
        }
    }

    @Override
    public void lock()
    {
        CameraController controller = this.factory.getBridge().get(IBridgeCamera.class).getCameraController();

        if (!controller.has(ProxyCameraController.class))
        {
            ProxyCameraController cameraController = new ProxyCameraController(5);
            Camera camera = controller.camera;

            cameraController.position.set(camera.position);
            cameraController.rotation.set(camera.rotation);
            cameraController.fov = camera.fov;

            controller.add(cameraController);
        }
    }

    @Override
    public boolean isLocked()
    {
        CameraController controller = this.factory.getBridge().get(IBridgeCamera.class).getCameraController();

        return controller.has(ProxyCameraController.class);
    }

    @Override
    public void set(double x, double y, double z, float yaw, float pitch, float roll, float fov)
    {
        ICameraController controller = this.factory.getBridge().get(IBridgeCamera.class).getCameraController().getCurrent();

        if (controller instanceof ProxyCameraController)
        {
            ProxyCameraController proxy = (ProxyCameraController) controller;

            proxy.position.set(x, y, z);
            proxy.rotation.set(MathUtils.toRad(pitch), MathUtils.toRad(yaw), MathUtils.toRad(roll));
            proxy.fov = MathUtils.toRad(fov);
        }
    }

    @Override
    public void unlock()
    {
        CameraController controller = this.factory.getBridge().get(IBridgeCamera.class).getCameraController();

        if (controller.has(ProxyCameraController.class))
        {
            controller.remove(ProxyCameraController.class);
        }
    }
}