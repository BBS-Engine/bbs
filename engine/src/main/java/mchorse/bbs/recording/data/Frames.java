package mchorse.bbs.recording.data;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Frames implements IMapSerializable
{
    public static final String GROUP_POSITION = "position";
    public static final String GROUP_ROTATION = "rotation";
    public static final String GROUP_DPAD = "dpad";
    public static final String GROUP_LEFT_STICK = "lstick";
    public static final String GROUP_RIGHT_STICK = "rstick";
    public static final String GROUP_TRIGGERS = "triggers";

    public static final Set<String> GROUPS = new HashSet<>(Arrays.asList(GROUP_POSITION, GROUP_ROTATION, GROUP_DPAD, GROUP_LEFT_STICK, GROUP_RIGHT_STICK, GROUP_TRIGGERS));

    public KeyframeChannel x = new KeyframeChannel();
    public KeyframeChannel y = new KeyframeChannel();
    public KeyframeChannel z = new KeyframeChannel();

    public KeyframeChannel vX = new KeyframeChannel();
    public KeyframeChannel vY = new KeyframeChannel();
    public KeyframeChannel vZ = new KeyframeChannel();

    public KeyframeChannel yaw = new KeyframeChannel();
    public KeyframeChannel pitch = new KeyframeChannel();
    public KeyframeChannel bodyYaw = new KeyframeChannel();

    public KeyframeChannel sneaking = new KeyframeChannel();
    public KeyframeChannel grounded = new KeyframeChannel();
    public KeyframeChannel fall = new KeyframeChannel();

    public KeyframeChannel stickLeftX = new KeyframeChannel();
    public KeyframeChannel stickLeftY = new KeyframeChannel();
    public KeyframeChannel stickRightX = new KeyframeChannel();
    public KeyframeChannel stickRightY = new KeyframeChannel();
    public KeyframeChannel triggerLeft = new KeyframeChannel();
    public KeyframeChannel triggerRight = new KeyframeChannel();

    public KeyframeChannel gamepad = new KeyframeChannel();

    private Map<String, KeyframeChannel> keyframes = new HashMap<>();

    public Frames()
    {
        this.keyframes.put("x", this.x);
        this.keyframes.put("y", this.y);
        this.keyframes.put("z", this.z);
        this.keyframes.put("vX", this.vX);
        this.keyframes.put("vY", this.vY);
        this.keyframes.put("vZ", this.vZ);
        this.keyframes.put("yaw", this.yaw);
        this.keyframes.put("pitch", this.pitch);
        this.keyframes.put("bodyYaw", this.bodyYaw);
        this.keyframes.put("sneaking", this.sneaking);
        this.keyframes.put("grounded", this.grounded);
        this.keyframes.put("fall", this.fall);
        this.keyframes.put("stick_lx", this.stickLeftX);
        this.keyframes.put("stick_ly", this.stickLeftY);
        this.keyframes.put("stick_rx", this.stickRightX);
        this.keyframes.put("stick_ry", this.stickRightY);
        this.keyframes.put("trigger_l", this.triggerLeft);
        this.keyframes.put("trigger_r", this.triggerRight);
        this.keyframes.put("gamepad", this.gamepad);
    }

    public Map<String, KeyframeChannel> getMap()
    {
        return Collections.unmodifiableMap(this.keyframes);
    }

    public void record(int tick, Entity entity, List<String> groups)
    {
        BasicComponent basic = entity.basic;

        boolean empty = groups == null || groups.isEmpty();
        boolean position = empty || groups.contains(GROUP_POSITION);
        boolean rotation = empty || groups.contains(GROUP_ROTATION);
        boolean dpad = empty || groups.contains(GROUP_DPAD);
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

            if (dpad) this.gamepad.insert(tick, component.gamepad);
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
        boolean dpad = empty || !groups.contains(GROUP_DPAD);
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

            if (dpad)
            {
                component.gamepad = (int) this.gamepad.interpolate(tick);
            }
        }
    }

    public void copy(Frames frames)
    {
        for (String key : this.keyframes.keySet())
        {
            this.keyframes.get(key).copy(frames.keyframes.get(key));
        }
    }

    @Override
    public void fromData(MapType data)
    {
        for (Map.Entry<String, KeyframeChannel> entry : this.keyframes.entrySet())
        {
            entry.getValue().fromData(data.getList(entry.getKey()));
        }
    }

    @Override
    public void toData(MapType data)
    {
        for (Map.Entry<String, KeyframeChannel> entry : this.keyframes.entrySet())
        {
            data.put(entry.getKey(), entry.getValue().toData());
        }
    }
}