package mchorse.bbs.world.entities.components;

import mchorse.bbs.utils.AABB;
import mchorse.bbs.world.World;
import org.joml.Vector3d;

import java.util.List;

public class CollisionComponent extends Component
{
    private static final int AXIS_X = 0b100;
    private static final int AXIS_Y = 0b010;
    private static final int AXIS_Z = 0b001;
    private static final int AXIS_ALL = AXIS_X | AXIS_Y | AXIS_Z;

    @Override
    public void preUpdate()
    {
        super.preUpdate();

        if (!this.entity.basic.manualControl)
        {
            this.applyCollisions(this.entity.world, this.entity.basic);
        }
    }

    /**
     * Apply collisions to the player
     */
    public void applyCollisions(World world, BasicComponent component)
    {
        int blockX = (int) Math.round(component.position.x);
        int blockY = (int) Math.round(component.position.y) - 1;
        int blockZ = (int) Math.round(component.position.z);

        if (component.grounded && !world.chunks.hasBlock(blockX, blockY, blockZ))
        {
            component.grounded = false;
        }

        if (component.velocity.x == 0 && component.velocity.y == 0 && component.velocity.z == 0)
        {
            return;
        }

        AABB hitbox = component.hitbox;
        double newX = component.position.x + component.velocity.x;
        double newY = component.position.y + component.velocity.y;
        double newZ = component.position.z + component.velocity.z;

        double halfW = hitbox.w / 2;
        double halfH = hitbox.h / 2;
        double halfD = hitbox.d / 2;

        double x1 = Math.min(component.position.x, newX) - halfW - 1;
        double y1 = Math.min(component.position.y, newY) - 1;
        double z1 = Math.min(component.position.z, newZ) - halfD - 1;
        double x2 = Math.max(component.position.x, newX) + halfW + 1;
        double y2 = Math.max(component.position.y, newY) + halfH * 2 + 1;
        double z2 = Math.max(component.position.z, newZ) + halfD + 1;

        List<AABB> aabbs = world.getCollisionAABBs(x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, component.velocity.x, component.velocity.y, component.velocity.z);

        if (aabbs.isEmpty())
        {
            return;
        }

        Vector3d step = new Vector3d(newX, newY, newZ).sub(component.position);
        double length = Math.max(step.length(), 0.00001);
        int axes = 0;

        for (int i = 0, c = (int) Math.ceil(length); i <= c; i++)
        {
            double factor = i == c ? 1 : i / length;

            hitbox.setPosition(
                component.position.x + step.x * factor - halfW,
                component.position.y + step.y * factor,
                component.position.z + step.z * factor - halfD
            );

            axes = this.applyCollision(component, hitbox, aabbs, axes);

            if (axes != 0)
            {
                break;
            }
        }
    }

    public int applyCollision(BasicComponent component, AABB hitbox, List<AABB> aabbs, int corrected)
    {
        double halfW = hitbox.w / 2;
        double halfD = hitbox.d / 2;

        for (AABB block : aabbs)
        {
            /* If player's AABB intersects with the block
             * then we got to push the player back */
            if (hitbox.intersects(block))
            {
                /* Push back player on Y axis */
                if (!component.grounded)
                {
                    if ((corrected & AXIS_Y) == 0)
                    {
                        if (hitbox.y < block.y + block.h && component.position.y >= block.y + block.h)
                        {
                            component.position.y = hitbox.y = block.y + block.h;
                            component.velocity.y = 0;
                            component.grounded = true;

                            corrected = corrected | AXIS_Y;

                            continue;
                        }
                        else if (hitbox.y + hitbox.h > block.y && component.position.y + hitbox.h <= block.y)
                        {
                            component.position.y = hitbox.y = block.y - hitbox.h;
                            component.velocity.y = 0;

                            corrected = corrected | AXIS_Y;

                            continue;
                        }
                    }
                }

                /* Push back player on X axis */
                if ((corrected & AXIS_X) == 0)
                {
                    if (hitbox.x < block.x + block.w && component.position.x - halfW >= block.x + block.w)
                    {
                        component.position.x = block.x + block.w + halfW;
                        hitbox.x = block.x + block.w;
                        component.velocity.x = 0;

                        corrected = corrected | AXIS_X;

                        continue;
                    }
                    else if (hitbox.x + hitbox.w > block.x && component.position.x + halfW <= block.x)
                    {
                        component.position.x = block.x - halfW;
                        hitbox.x = block.x - halfW * 2;
                        component.velocity.x = 0;

                        corrected = corrected | AXIS_X;

                        continue;
                    }
                }

                /* Push back player on Z axis */
                if ((corrected & AXIS_Z) == 0)
                {
                    if (hitbox.z < block.z + block.d && component.position.z - halfD >= block.z + block.d)
                    {
                        component.position.z = block.z + block.d + halfD;
                        hitbox.z = block.z + block.d;
                        component.velocity.z = 0;

                        corrected = corrected | AXIS_Z;
                    }
                    else if (hitbox.z + hitbox.d > block.z && component.position.z + halfD <= block.z)
                    {
                        component.position.z = block.z - halfD;
                        hitbox.z = block.z - halfD * 2;
                        component.velocity.z = 0;

                        corrected = corrected | AXIS_Z;
                    }
                }
            }
        }

        return corrected;
    }
}