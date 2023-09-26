package mchorse.bbs.cubic;

import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.math.Variable;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;

public class MolangHelper
{
    public static void registerVars(MolangParser parser)
    {
        parser.register("query.anim_time");
        parser.register("query.ground_speed");
        parser.register("query.yaw_speed");

        /* Additional Chameleon specific variables */
        parser.register("query.head_yaw");
        parser.register("query.head_pitch");

        parser.register("query.velocity");
        parser.register("query.age");

        /* Cool joystick variables */
        parser.register("joystick.l_x");
        parser.register("joystick.l_y");
        parser.register("joystick.r_x");
        parser.register("joystick.r_y");
        parser.register("joystick.l_trigger");
        parser.register("joystick.r_trigger");
        parser.register("extra1.x");
        parser.register("extra1.y");
        parser.register("extra2.x");
        parser.register("extra2.y");
    }

    public static void setMolangVariables(MolangParser parser, Entity target, float frame, float transition)
    {
        double dx = 0;
        double dz = 0;
        double yawSpeed = 0;
        double headYaw = 0;
        double headPitch = 0;
        double velocity = 0;
        double age = 0;

        if (target != null)
        {
            BasicComponent basic = target.basic;
            float yawHead = Interpolations.lerp(basic.prevRotation.y, basic.rotation.y, transition);
            float bodyYaw = Interpolations.lerp(basic.prevRotation.z, basic.rotation.z, transition);

            dx = basic.velocity.x;
            dz = basic.velocity.z;
            yawSpeed = Interpolations.lerp(basic.prevRotation.z - basic.prevPrevRotationZ, basic.rotation.z - basic.prevRotation.z, transition);
            headYaw = Math.toDegrees(yawHead - bodyYaw);
            headPitch = Math.toDegrees(Interpolations.lerp(basic.prevRotation.x, basic.rotation.x, transition));
            velocity = Math.sqrt(dx * dx + basic.velocity.y * basic.velocity.y + dz * dz);

            /* There is still a tiny bit of vertical velocity (gravity) when an
             * entity stands still, so set it to zero in that case */
            if (basic.grounded && basic.velocity.y < 0 && (Math.abs(dx) < 0.001 || Math.abs(dz) < 0.001))
            {
                velocity = 0;
            }

            age = basic.ticks + transition;

            PlayerComponent playerComponent = target.get(PlayerComponent.class);

            if (playerComponent != null)
            {
                float[] prev = playerComponent.prevSticks;
                float[] sticks = playerComponent.sticks;

                parser.setValue("joystick.l_x", Interpolations.lerp(prev[0], sticks[0], transition));
                parser.setValue("joystick.l_y", Interpolations.lerp(prev[1], sticks[1], transition));
                parser.setValue("joystick.r_x", Interpolations.lerp(prev[2], sticks[2], transition));
                parser.setValue("joystick.r_y", Interpolations.lerp(prev[3], sticks[3], transition));
                parser.setValue("joystick.l_trigger", Interpolations.lerp(prev[4], sticks[4], transition));
                parser.setValue("joystick.r_trigger", Interpolations.lerp(prev[5], sticks[5], transition));
                parser.setValue("extra1.x", Interpolations.lerp(prev[6], sticks[6], transition));
                parser.setValue("extra1.y", Interpolations.lerp(prev[7], sticks[7], transition));
                parser.setValue("extra2.x", Interpolations.lerp(prev[8], sticks[8], transition));
                parser.setValue("extra2.y", Interpolations.lerp(prev[9], sticks[9], transition));
            }
        }
        else
        {
            parser.setValue("joystick.l_x", 0);
            parser.setValue("joystick.l_y", 0);
            parser.setValue("joystick.r_x", 0);
            parser.setValue("joystick.r_y", 0);
            parser.setValue("joystick.l_bumper", 0);
            parser.setValue("joystick.r_bumper", 0);
            parser.setValue("extra1.x", 0);
            parser.setValue("extra1.y", 0);
            parser.setValue("extra2.x", 0);
            parser.setValue("extra2.y", 0);
        }

        float groundSpeed = (float) Math.sqrt(dx * dx + dz * dz);

        parser.setValue("query.anim_time", frame / 20);
        parser.setValue("query.ground_speed", groundSpeed);
        parser.setValue("query.yaw_speed", yawSpeed);
        parser.setValue("query.head_yaw", headYaw);
        parser.setValue("query.head_pitch", headPitch);
        parser.setValue("query.velocity", velocity);
        parser.setValue("query.age", age);
    }

    /**
     * Get value from given value of a keyframe (end or start)
     *
     * This method is responsible for processing keyframe value, because
     * for some reason constant values are exported in radians, while molang
     * expressions are in degrees
     *
     * Plus X and Y axis of rotation are inverted for some reason ...
     */
    public static double getValue(MolangExpression value, Component component, Axis axis)
    {
        double out = value.get();

        if (component == Component.ROTATION)
        {
            if (axis == Axis.X || axis == Axis.Y)
            {
                out *= -1;
            }
        }
        else if (component == Component.SCALE)
        {
            out = out - 1;
        }

        return out;
    }

    /**
     * Component enum determines which part of the animation is being
     * calculated
     */
    public static enum Component
    {
        POSITION, ROTATION, SCALE
    }
}