package mchorse.bbs.film.values;

import mchorse.bbs.camera.values.ValueKeyframeChannel;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;

import java.util.List;

public class ValueKeyframes extends ValueGroup
{
    public static final String GROUP_POSITION = "position";
    public static final String GROUP_ROTATION = "rotation";
    public static final String GROUP_LEFT_STICK = "lstick";
    public static final String GROUP_RIGHT_STICK = "rstick";
    public static final String GROUP_TRIGGERS = "triggers";

    public final ValueKeyframeChannel x = new ValueKeyframeChannel("x");
    public final ValueKeyframeChannel y = new ValueKeyframeChannel("y");
    public final ValueKeyframeChannel z = new ValueKeyframeChannel("z");

    public final ValueKeyframeChannel vX = new ValueKeyframeChannel("vX");
    public final ValueKeyframeChannel vY = new ValueKeyframeChannel("vY");
    public final ValueKeyframeChannel vZ = new ValueKeyframeChannel("vZ");

    public final ValueKeyframeChannel yaw = new ValueKeyframeChannel("yaw");
    public final ValueKeyframeChannel pitch = new ValueKeyframeChannel("pitch");
    public final ValueKeyframeChannel bodyYaw = new ValueKeyframeChannel("bodyYaw");

    public final ValueKeyframeChannel sneaking = new ValueKeyframeChannel("sneaking");
    public final ValueKeyframeChannel grounded = new ValueKeyframeChannel("grounded");
    public final ValueKeyframeChannel fall = new ValueKeyframeChannel("fall");

    public final ValueKeyframeChannel stickLeftX = new ValueKeyframeChannel("stick_lx");
    public final ValueKeyframeChannel stickLeftY = new ValueKeyframeChannel("stick_ly");
    public final ValueKeyframeChannel stickRightX = new ValueKeyframeChannel("stick_rx");
    public final ValueKeyframeChannel stickRightY = new ValueKeyframeChannel("stick_ry");
    public final ValueKeyframeChannel triggerLeft = new ValueKeyframeChannel("trigger_l");
    public final ValueKeyframeChannel triggerRight = new ValueKeyframeChannel("trigger_r");

    public ValueKeyframes(String id)
    {
        super(id);

        this.add(this.x);
        this.add(this.y);
        this.add(this.z);
        this.add(this.vX);
        this.add(this.vY);
        this.add(this.vZ);
        this.add(this.yaw);
        this.add(this.pitch);
        this.add(this.bodyYaw);
        this.add(this.sneaking);
        this.add(this.grounded);
        this.add(this.fall);
        this.add(this.stickLeftX);
        this.add(this.stickLeftY);
        this.add(this.stickRightX);
        this.add(this.stickRightY);
        this.add(this.triggerLeft);
        this.add(this.triggerRight);
    }

    public void record(int tick, Entity entity, List<String> groups)
    {
        BasicComponent basic = entity.basic;

        boolean empty = groups == null || groups.isEmpty();
        boolean position = empty || groups.contains(GROUP_POSITION);
        boolean rotation = empty || groups.contains(GROUP_ROTATION);
        boolean leftStick = empty || groups.contains(GROUP_LEFT_STICK);
        boolean rightStick = empty || groups.contains(GROUP_RIGHT_STICK);
        boolean triggers = empty || groups.contains(GROUP_TRIGGERS);

        /* Position and rotation */
        if (position)
        {
            this.x.get().insert(tick, basic.position.x);
            this.y.get().insert(tick, basic.position.y);
            this.z.get().insert(tick, basic.position.z);

            this.vX.get().insert(tick, basic.velocity.x);
            this.vY.get().insert(tick, basic.velocity.y);
            this.vZ.get().insert(tick, basic.velocity.z);

            this.fall.get().insert(tick, basic.fall);
        }

        this.sneaking.get().insert(tick, basic.sneak ? 1D : 0D);
        this.grounded.get().insert(tick, basic.grounded ? 1D : 0D);

        if (rotation)
        {
            this.yaw.get().insert(tick, basic.rotation.y);
            this.pitch.get().insert(tick, basic.rotation.x);
            this.bodyYaw.get().insert(tick, basic.rotation.z);
        }

        PlayerComponent component = entity.get(PlayerComponent.class);

        if (component != null)
        {
            if (leftStick)
            {
                this.stickLeftX.get().insert(tick, component.sticks[0]);
                this.stickLeftY.get().insert(tick, component.sticks[1]);
            }

            if (rightStick)
            {
                this.stickRightX.get().insert(tick, component.sticks[2]);
                this.stickRightY.get().insert(tick, component.sticks[3]);
            }

            if (triggers)
            {
                this.triggerLeft.get().insert(tick, component.sticks[4]);
                this.triggerRight.get().insert(tick, component.sticks[5]);
            }
        }
    }

    /**
     * Apply a frame at given tick on the given entity.
     */
    public void apply(int tick, Entity entity, List<String> groups)
    {
        BasicComponent basic = entity.basic;
        boolean empty = groups == null || groups.isEmpty();
        boolean position = empty || !groups.contains(GROUP_POSITION);
        boolean rotation = empty || !groups.contains(GROUP_ROTATION);
        boolean leftStick = empty || !groups.contains(GROUP_LEFT_STICK);
        boolean rightStick = empty || !groups.contains(GROUP_RIGHT_STICK);
        boolean triggers = empty || !groups.contains(GROUP_TRIGGERS);

        if (position)
        {
            basic.setPosition(this.x.get().interpolate(tick), this.y.get().interpolate(tick), this.z.get().interpolate(tick));
            basic.velocity.set((float) this.vX.get().interpolate(tick), (float) this.vY.get().interpolate(tick), (float) this.vZ.get().interpolate(tick));
            basic.fall = (float) this.fall.get().interpolate(tick);
        }

        if (rotation)
        {
            basic.setRotation((float) this.pitch.get().interpolate(tick), (float) this.yaw.get().interpolate(tick));
            basic.rotation.z = (float) this.bodyYaw.get().interpolate(tick);
        }

        /* Motion and fall distance */
        basic.sneak = this.sneaking.get().interpolate(tick) != 0D;
        basic.grounded = this.grounded.get().interpolate(tick) != 0D;

        PlayerComponent component = entity.get(PlayerComponent.class);

        if (component != null)
        {
            if (leftStick)
            {
                component.sticks[0] = (float) this.stickLeftX.get().interpolate(tick);
                component.sticks[1] = (float) this.stickLeftY.get().interpolate(tick);
            }

            if (rightStick)
            {
                component.sticks[2] = (float) this.stickRightX.get().interpolate(tick);
                component.sticks[3] = (float) this.stickRightY.get().interpolate(tick);
            }

            if (triggers)
            {
                component.sticks[4] = (float) this.triggerLeft.get().interpolate(tick);
                component.sticks[5] = (float) this.triggerRight.get().interpolate(tick);
            }
        }
    }
}