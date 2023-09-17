package mchorse.bbs.film.replays;

import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;

import java.util.Arrays;
import java.util.List;

public class ReplayKeyframes extends ValueGroup
{
    public static final String GROUP_POSITION = "position";
    public static final String GROUP_ROTATION = "rotation";
    public static final String GROUP_LEFT_STICK = "lstick";
    public static final String GROUP_RIGHT_STICK = "rstick";
    public static final String GROUP_TRIGGERS = "triggers";

    public static final List<String> CURATED_CHANNELS = Arrays.asList("x", "y", "z", "pitch", "yaw", "bodyYaw", "sneaking", "stick_lx", "stick_ly", "stick_rx", "stick_ry", "trigger_l", "trigger_r", "grounded", "vX", "vY", "vZ");

    public final KeyframeChannel x = new KeyframeChannel("x");
    public final KeyframeChannel y = new KeyframeChannel("y");
    public final KeyframeChannel z = new KeyframeChannel("z");

    public final KeyframeChannel vX = new KeyframeChannel("vX");
    public final KeyframeChannel vY = new KeyframeChannel("vY");
    public final KeyframeChannel vZ = new KeyframeChannel("vZ");

    public final KeyframeChannel yaw = new KeyframeChannel("yaw");
    public final KeyframeChannel pitch = new KeyframeChannel("pitch");
    public final KeyframeChannel bodyYaw = new KeyframeChannel("bodyYaw");

    public final KeyframeChannel sneaking = new KeyframeChannel("sneaking");
    public final KeyframeChannel grounded = new KeyframeChannel("grounded");
    public final KeyframeChannel fall = new KeyframeChannel("fall");

    public final KeyframeChannel stickLeftX = new KeyframeChannel("stick_lx");
    public final KeyframeChannel stickLeftY = new KeyframeChannel("stick_ly");
    public final KeyframeChannel stickRightX = new KeyframeChannel("stick_rx");
    public final KeyframeChannel stickRightY = new KeyframeChannel("stick_ry");
    public final KeyframeChannel triggerLeft = new KeyframeChannel("trigger_l");
    public final KeyframeChannel triggerRight = new KeyframeChannel("trigger_r");

    public ReplayKeyframes(String id)
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
            this.x.insert(tick, basic.position.x);
            this.y.insert(tick, basic.position.y);
            this.z.insert(tick, basic.position.z);

            this.vX.insert(tick, basic.velocity.x);
            this.vY.insert(tick, basic.velocity.y);
            this.vZ.insert(tick, basic.velocity.z);

            this.fall.insert(tick, basic.fall);
        }

        this.sneaking.insert(tick, basic.sneak ? 1D : 0D);
        this.grounded.insert(tick, basic.grounded ? 1D : 0D);

        if (rotation)
        {
            this.yaw.insert(tick, basic.rotation.y);
            this.pitch.insert(tick, basic.rotation.x);
            this.bodyYaw.insert(tick, basic.rotation.z);
        }

        PlayerComponent component = entity.get(PlayerComponent.class);

        if (component != null)
        {
            if (leftStick)
            {
                this.stickLeftX.insert(tick, component.sticks[0]);
                this.stickLeftY.insert(tick, component.sticks[1]);
            }

            if (rightStick)
            {
                this.stickRightX.insert(tick, component.sticks[2]);
                this.stickRightY.insert(tick, component.sticks[3]);
            }

            if (triggers)
            {
                this.triggerLeft.insert(tick, component.sticks[4]);
                this.triggerRight.insert(tick, component.sticks[5]);
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
            basic.setPosition(this.x.interpolate(tick), this.y.interpolate(tick), this.z.interpolate(tick));
            basic.velocity.set((float) this.vX.interpolate(tick), (float) this.vY.interpolate(tick), (float) this.vZ.interpolate(tick));
            basic.fall = (float) this.fall.interpolate(tick);
        }

        if (rotation)
        {
            basic.setRotation((float) this.pitch.interpolate(tick), (float) this.yaw.interpolate(tick));
            basic.rotation.z = (float) this.bodyYaw.interpolate(tick);
        }

        /* Motion and fall distance */
        basic.sneak = this.sneaking.interpolate(tick) != 0D;
        basic.grounded = this.grounded.interpolate(tick) != 0D;

        PlayerComponent component = entity.get(PlayerComponent.class);

        if (component != null)
        {
            if (leftStick)
            {
                component.sticks[0] = (float) this.stickLeftX.interpolate(tick);
                component.sticks[1] = (float) this.stickLeftY.interpolate(tick);
            }

            if (rightStick)
            {
                component.sticks[2] = (float) this.stickRightX.interpolate(tick);
                component.sticks[3] = (float) this.stickRightY.interpolate(tick);
            }

            if (triggers)
            {
                component.sticks[4] = (float) this.triggerLeft.interpolate(tick);
                component.sticks[5] = (float) this.triggerRight.interpolate(tick);
            }
        }
    }
}