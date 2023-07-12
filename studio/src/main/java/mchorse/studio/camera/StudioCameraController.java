package mchorse.studio.camera;

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
import mchorse.studio.StudioController;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class StudioCameraController implements ICameraController
{
    private StudioController controller;
    private RayTraceResult result = new RayTraceResult();

    public StudioCameraController(StudioController controller)
    {
        this.controller = controller;
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        Entity controller = this.controller.getController();

        if (controller == null)
        {
            return;
        }

        Vector3d position = new Vector3d();
        Vector3f rotation = new Vector3f();
        float distance = 5F;

        BasicComponent basic = controller.basic;
        boolean back = this.controller.back;

        position.set(basic.prevPosition);
        position.lerp(basic.position, transition);
        position.y += basic.getEyeHeight();

        rotation.set(basic.prevRotation);
        rotation.lerp(basic.rotation, transition);

        camera.fov = MathUtils.toRad(70F);

        Vector3f rotate = back
            ? Matrices.rotation(-rotation.x, -rotation.y)
            : Matrices.rotation(rotation.x, MathUtils.PI - rotation.y);
        World world = controller.world;

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

        if (back)
        {
            camera.rotation.set(rotation.x, rotation.y, 0);
        }
        else
        {
            camera.rotation.set(-rotation.x, MathUtils.PI + rotation.y, 0);
        }
    }
}