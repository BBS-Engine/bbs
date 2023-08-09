package mchorse.bbs.recording.data;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frames implements IMapSerializable
{
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

    public KeyframeChannel stick1 = new KeyframeChannel();
    public KeyframeChannel stick2 = new KeyframeChannel();
    public KeyframeChannel stick3 = new KeyframeChannel();
    public KeyframeChannel stick4 = new KeyframeChannel();
    public KeyframeChannel stick5 = new KeyframeChannel();
    public KeyframeChannel stick6 = new KeyframeChannel();

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
        this.keyframes.put("stick1", this.stick1);
        this.keyframes.put("stick2", this.stick2);
        this.keyframes.put("stick3", this.stick3);
        this.keyframes.put("stick4", this.stick4);
        this.keyframes.put("stick5", this.stick5);
        this.keyframes.put("stick6", this.stick6);
        this.keyframes.put("gamepad", this.gamepad);
    }

    public void record(int tick, Entity entity, List<String> groups)
    {
        BasicComponent basic = entity.basic;

        boolean empty = groups == null || groups.isEmpty();
        boolean position = empty || groups.contains(Frame.GROUP_POSITION);
        boolean rotation = empty || groups.contains(Frame.GROUP_ROTATION);
        boolean dpad = empty || groups.contains(Frame.GROUP_DPAD);
        boolean leftStick = empty || groups.contains(Frame.GROUP_LEFT_STICK);
        boolean rightStick = empty || groups.contains(Frame.GROUP_RIGHT_STICK);
        boolean triggers = empty || groups.contains(Frame.GROUP_TRIGGERS);

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
                this.stick1.insert(tick, component.sticks[0]);
                this.stick2.insert(tick, component.sticks[1]);
            }

            if (rightStick)
            {
                this.stick3.insert(tick, component.sticks[2]);
                this.stick4.insert(tick, component.sticks[3]);
            }

            if (triggers)
            {
                this.stick5.insert(tick, component.sticks[4]);
                this.stick6.insert(tick, component.sticks[5]);
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
        boolean position = empty || !groups.contains(Frame.GROUP_POSITION);
        boolean rotation = empty || !groups.contains(Frame.GROUP_ROTATION);
        boolean dpad = empty || !groups.contains(Frame.GROUP_DPAD);
        boolean leftStick = empty || !groups.contains(Frame.GROUP_LEFT_STICK);
        boolean rightStick = empty || !groups.contains(Frame.GROUP_RIGHT_STICK);
        boolean triggers = empty || !groups.contains(Frame.GROUP_TRIGGERS);

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
                component.sticks[0] = (float) this.stick1.interpolate(tick);
                component.sticks[1] = (float) this.stick2.interpolate(tick);
            }

            if (rightStick)
            {
                component.sticks[2] = (float) this.stick3.interpolate(tick);
                component.sticks[3] = (float) this.stick4.interpolate(tick);
            }

            if (triggers)
            {
                component.sticks[4] = (float) this.stick5.interpolate(tick);
                component.sticks[5] = (float) this.stick6.interpolate(tick);
            }

            if (dpad)
            {
                component.gamepad = (int) this.gamepad.interpolate(tick);
            }
        }
    }

    @Override
    public void fromData(MapType data)
    {
        for (Map.Entry<String, KeyframeChannel> entry : this.keyframes.entrySet())
        {
            entry.getValue().fromData(data.getList(entry.getKey()));
        }

        for (String key : data.keys())
        {
            KeyframeChannel keyframeChannel = this.keyframes.get(key);

            if (keyframeChannel != null)
            {
                keyframeChannel.fromData(data.getList(key));
            }
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