package mchorse.bbs.ui.film.controller;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.controller.ICameraController;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;

public class OrbitFilmCameraController implements ICameraController
{
    private UIFilmController controller;

    private boolean orbiting;
    private Vector2f orbitRotation = new Vector2f();
    private Vector2i orbitLast = new Vector2i();
    private float orbitDistance = 5F;

    public OrbitFilmCameraController(UIFilmController controller)
    {
        this.controller = controller;
    }

    public void start(UIContext context)
    {
        this.orbiting = true;
        this.orbitLast.set(context.mouseX, context.mouseY);
    }

    public void handleDistance(UIContext context)
    {
        this.orbitDistance = MathUtils.clamp(this.orbitDistance + Math.copySign(1, context.mouseWheel), 0F, 100F);
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

            this.orbitRotation.add(
                -(y - this.orbitLast.y) / 50F,
                -(x - this.orbitLast.x) / 50F
            );

            this.orbitLast.set(x, y);
        }
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        int index = this.controller.panel.replays.replays.getIndex();

        if (CollectionUtils.inRange(this.controller.entities, index))
        {
            Entity entity = this.controller.entities.get(index);
            Vector3d offset = new Vector3d(Matrices.rotation(this.orbitRotation.x, this.orbitRotation.y));

            offset.mul(this.orbitDistance);
            entity.basic.prevPosition.lerp(entity.basic.position, transition, camera.position);
            camera.position.add(offset);
            camera.position.add(0, entity.basic.hitbox.h / 2, 0);
            camera.rotation.set(-this.orbitRotation.x, -this.orbitRotation.y, 0);
        }
    }

    @Override
    public int getPriority()
    {
        return 20;
    }
}