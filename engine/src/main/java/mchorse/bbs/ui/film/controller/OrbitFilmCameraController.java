package mchorse.bbs.ui.film.controller;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.controller.ICameraController;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;

public class OrbitFilmCameraController implements ICameraController
{
    private UIFilmController controller;

    public boolean enabled;

    private boolean orbiting;
    private Vector2f rotation = new Vector2f();
    private Vector2i last = new Vector2i();
    private float distance = 5F;

    public OrbitFilmCameraController(UIFilmController controller)
    {
        this.controller = controller;
    }

    public float getDistance()
    {
        return this.distance;
    }

    public void start(UIContext context)
    {
        this.orbiting = true;
        this.last.set(context.mouseX, context.mouseY);
    }

    public void handleDistance(UIContext context)
    {
        this.distance = MathUtils.clamp(this.distance + Math.copySign(1, context.mouseWheel), 0F, 100F);
    }

    public void stop()
    {
        this.orbiting = false;
    }

    public void handleOrbiting(UIContext context)
    {
        if (this.orbiting)
        {
            int x = context.mouseX;
            int y = context.mouseY;

            this.rotation.add(
                -(y - this.last.y) / 50F,
                -(x - this.last.x) / 50F
            );

            this.last.set(x, y);
        }
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        Entity entity = this.controller.getCurrentEntity();

        if (entity != null)
        {
            Vector3d offset = new Vector3d(Matrices.rotation(this.rotation.x, this.rotation.y));

            offset.mul(this.distance);
            entity.basic.prevPosition.lerp(entity.basic.position, transition, camera.position);
            camera.position.add(offset);
            camera.position.add(0, entity.basic.hitbox.h / 2, 0);
            camera.rotation.set(-this.rotation.x, -this.rotation.y, 0);
        }
    }

    @Override
    public int getPriority()
    {
        return 20;
    }
}