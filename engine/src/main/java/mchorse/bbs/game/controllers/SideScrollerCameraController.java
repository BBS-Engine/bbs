package mchorse.bbs.game.controllers;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.controller.ICameraController;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class SideScrollerCameraController implements ICameraController, ITickable
{
    private SideScrollerGameController controller;

    private Vector3d prevPosiiton;
    private Vector3d position;
    private Vector3d target = new Vector3d();

    public SideScrollerCameraController(SideScrollerGameController controller)
    {
        this.controller = controller;
    }

    @Override
    public void update()
    {
        if (this.position != null)
        {
            this.prevPosiiton.set(this.position);

            this.position.lerp(this.target, 0.33F);
        }
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        IBridge bridge = this.controller.getBridge();
        Entity controller = bridge.get(IBridgePlayer.class).getController();

        if (controller == null)
        {
            return;
        }

        BasicComponent basic = controller.basic;
        Vector3f cameraOffset = this.controller.cameraOffset;

        camera.fov = MathUtils.toRad(this.controller.fov);
        camera.rotation.set(0, 0, 0);
        this.target.set(basic.prevPosition);
        this.target.lerp(basic.position, transition);
        this.target.add(basic.velocity.x * cameraOffset.x, basic.getEyeHeight() * cameraOffset.y + basic.velocity.y, cameraOffset.z);

        if (this.position == null)
        {
            camera.position.set(this.target);

            this.position = new Vector3d(this.target);
            this.prevPosiiton = new Vector3d(this.target);
        }
        else
        {
            camera.position.set(this.prevPosiiton);
            camera.position.lerp(this.position, transition);
        }
    }
}