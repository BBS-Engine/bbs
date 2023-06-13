package mchorse.bbs.game.controllers;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.controller.ICameraController;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ThirdPersonCameraController implements ICameraController
{
    private ThirdPersonGameController controller;
    private RayTraceResult result = new RayTraceResult();

    public ThirdPersonCameraController(ThirdPersonGameController controller)
    {
        this.controller = controller;
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

        Vector3d position = new Vector3d();
        Vector3f rotation = new Vector3f();
        float distance = this.controller.cameraOffset.z;

        BasicComponent basic = controller.basic;

        position.set(basic.prevPosition);
        position.lerp(basic.position, transition);
        position.y += basic.getEyeHeight();

        rotation.set(basic.prevRotation);
        rotation.lerp(basic.rotation, transition);

        camera.fov = MathUtils.toRad(this.controller.fov);

        if (this.controller.firstPerson)
        {
            camera.position.set(position);
            camera.rotation.set(rotation.x, rotation.y, 0);

            return;
        }

        Vector3f rotate = this.controller.back
            ? Matrices.rotation(-rotation.x, -rotation.y)
            : Matrices.rotation(rotation.x, MathUtils.PI - rotation.y);
        World world = bridge.get(IBridgeWorld.class).getWorld();

        RayTracer.trace(this.result, world.chunks, position, rotate, distance, true, (b) ->
        {
            IBlockVariant block = world.chunks.getBlock(b.block.x, b.block.y, b.block.z);

            return block.getModel().opaque;
        });

        if (this.result.type == RayTraceType.BLOCK)
        {
            distance = (float) position.distance(this.result.hit) - 0.1F;
        }

        rotate.mul(distance);
        position.add(rotate);

        camera.position.set(position);

        if (this.controller.back)
        {
            camera.rotation.set(rotation.x, rotation.y, 0);
        }
        else
        {
            camera.rotation.set(-rotation.x, MathUtils.PI + rotation.y, 0);
        }
    }
}