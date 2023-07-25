package mchorse.bbs.recording.data;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Recording frame class
 *
 * This class stores state data about the player in the specific frame that was
 * captured.
 */
public class Frame implements IMapSerializable
{
    public static final String GROUP_POSITION = "position";
    public static final String GROUP_ROTATION = "rotation";
    public static final String GROUP_DPAD = "dpad";
    public static final String GROUP_LEFT_STICK = "lstick";
    public static final String GROUP_RIGHT_STICK = "rstick";
    public static final String GROUP_TRIGGERS = "triggers";

    public static final Set<String> PROPERTIES = new HashSet<String>(Arrays.asList("x", "y", "z", "yaw", "pitch", "fall", "sprinting", "sneaking", "roll"));
    public static final Set<String> GROUPS = new HashSet<String>(Arrays.asList(GROUP_POSITION, GROUP_ROTATION, GROUP_DPAD, GROUP_LEFT_STICK, GROUP_RIGHT_STICK, GROUP_TRIGGERS));

    /* Position */
    public double x;
    public double y;
    public double z;

    public float vX;
    public float vY;
    public float vZ;

    /* Rotation */
    public float yaw;
    public float pitch;
    public float bodyYaw;

    /* Fall distance */
    public float fall;

    /* Entity flags */
    public boolean isSneaking;
    public boolean isSprinting;
    public boolean grounded;

    /* Client data */
    public int color = Colors.WHITE;
    public float roll;

    /* Joystick data */
    public float[] sticks = new float[6];
    public int gamepad;

    public List<Action> actions = new ArrayList<Action>();

    public static double get(String property, Frame frame)
    {
        switch (property)
        {
            case "x": return frame.x;
            case "y": return frame.y;
            case "z": return frame.z;
            case "yaw": return frame.yaw;
            case "pitch": return frame.pitch;
            case "fall": return frame.fall;
            case "sprinting": return frame.isSprinting ? 1 : 0;
            case "sneaking": return frame.isSneaking ? 1 : 0;
            case "roll": return frame.roll;
        }

        return 0;
    }

    public static void set(String property, Frame frame, double value)
    {
        switch (property)
        {
            case "x": frame.x = value; break;
            case "y": frame.y = value; break;
            case "z": frame.z = value; break;
            case "yaw": frame.yaw = (float) value; break;
            case "pitch": frame.pitch = (float) value; break;
            case "fall": frame.fall = (float) value; break;
            case "sprinting": frame.isSprinting = value == 1; break;
            case "sneaking": frame.isSneaking = value == 1; break;
            case "roll": frame.roll = (float) value; break;
        }
    }

    /* Methods for retrieving/applying state data */

    public void fromEntity(Entity entity)
    {
        BasicComponent basic = entity.basic;

        /* Position and rotation */
        this.x = basic.position.x;
        this.y = basic.position.y;
        this.z = basic.position.z;

        this.vX = basic.velocity.x;
        this.vY = basic.velocity.y;
        this.vZ = basic.velocity.z;

        this.yaw = basic.rotation.y;
        this.pitch = basic.rotation.x;
        this.bodyYaw = basic.rotation.z;

        this.fall = basic.fall;

        /* States */
        // TODO: ? this.isSprinting = basic.isSprinting();
        this.isSneaking = basic.sneak;
        this.grounded = basic.grounded;

        PlayerComponent component = entity.get(PlayerComponent.class);

        if (component != null)
        {
            for (int i = 0; i < this.sticks.length; i++)
            {
                this.sticks[i] = component.sticks[i];
            }

            this.gamepad = component.gamepad;
        }
    }

    public void lerp(Frame a, Frame b, float x)
    {
        this.x = Interpolations.lerp(a.x, b.x, x);
        this.y = Interpolations.lerp(a.y, b.y, x);
        this.z = Interpolations.lerp(a.z, b.z, x);

        this.yaw = Interpolations.lerp(a.yaw, b.yaw, x);
        this.pitch = Interpolations.lerp(a.pitch, b.pitch, x);
        this.bodyYaw = Interpolations.lerp(a.bodyYaw, b.bodyYaw, x);

        this.fall = Interpolations.lerp(a.fall, b.fall, x);

        this.roll = Interpolations.lerp(a.roll, b.roll, x);

        for (int i = 0; i < a.sticks.length; i++)
        {
            this.sticks[i] = Interpolations.lerp(a.sticks[i], b.sticks[i], x);
        }
    }

    public void apply(Entity entity, List<String> groups)
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
            basic.setPosition(this.x, this.y, this.z);
            basic.velocity.set(this.vX, this.vY, this.vZ);
            basic.fall = this.fall;
        }
        if (rotation) basic.setRotation(this.pitch, this.yaw);
        if (rotation) basic.rotation.z = this.bodyYaw;

        /* Motion and fall distance */
        basic.sneak = this.isSneaking;
        basic.grounded = this.grounded;

        PlayerComponent component = entity.get(PlayerComponent.class);

        if (component != null)
        {
            if (leftStick)
            {
                component.sticks[0] = this.sticks[0];
                component.sticks[1] = this.sticks[1];
            }

            if (rightStick)
            {
                component.sticks[2] = this.sticks[2];
                component.sticks[3] = this.sticks[3];
            }

            if (triggers)
            {
                component.sticks[4] = this.sticks[4];
                component.sticks[5] = this.sticks[5];
            }

            if (dpad)
            {
                component.gamepad = this.gamepad;
            }
        }
    }

    /**
     * Create a copy of this frame 
     */
    public Frame copy()
    {
        Frame frame = new Frame();

        frame.fromData(this.toData());

        return frame;
    }

    public void copy(Frame oldFrame, List<String> groups)
    {
        boolean empty = groups == null || groups.isEmpty();
        boolean position = empty || groups.contains(GROUP_POSITION);
        boolean rotation = empty || groups.contains(GROUP_ROTATION);
        boolean dpad = empty || groups.contains(GROUP_DPAD);
        boolean leftStick = empty || groups.contains(GROUP_LEFT_STICK);
        boolean rightStick = empty || groups.contains(GROUP_RIGHT_STICK);
        boolean triggers = empty || groups.contains(GROUP_TRIGGERS);

        if (position)
        {
            this.x = oldFrame.x;
            this.y = oldFrame.y;
            this.z = oldFrame.z;

            this.vX = oldFrame.vX;
            this.vY = oldFrame.vY;
            this.vZ = oldFrame.vZ;
            this.fall = oldFrame.fall;
        }

        if (rotation)
        {
            this.yaw = oldFrame.yaw;
            this.pitch = oldFrame.pitch;
            this.bodyYaw = oldFrame.bodyYaw;
        }

        this.isSneaking = oldFrame.isSneaking;
        this.isSprinting = oldFrame.isSprinting;
        this.grounded = oldFrame.grounded;

        this.color = oldFrame.color;
        this.roll = oldFrame.roll;

        if (leftStick)
        {
            this.sticks[0] = oldFrame.sticks[0];
            this.sticks[1] = oldFrame.sticks[1];
        }

        if (rightStick)
        {
            this.sticks[2] = oldFrame.sticks[2];
            this.sticks[3] = oldFrame.sticks[3];
        }

        if (triggers)
        {
            this.sticks[4] = oldFrame.sticks[4];
            this.sticks[5] = oldFrame.sticks[5];
        }

        if (dpad)
        {
            this.gamepad = oldFrame.gamepad;
        }
    }

    public void copyActions(Frame oldFrame)
    {
        this.actions.clear();

        for (Action action : oldFrame.actions)
        {
            this.actions.add(BBS.getFactoryActions().fromData(BBS.getFactoryActions().toData(action)));
        }
    }

    @Override
    public void toData(MapType data)
    {
        data.putDouble("x", this.x);
        data.putDouble("y", this.y);
        data.putDouble("z", this.z);

        data.putFloat("vy", this.vX);
        data.putFloat("vx", this.vY);
        data.putFloat("vz", this.vZ);

        data.putFloat("ry", this.yaw);
        data.putFloat("rx", this.pitch);
        data.putFloat("rz", this.bodyYaw);

        data.putFloat("fall", this.fall);

        data.putBool("sneaking", this.isSneaking);
        data.putBool("sprinting", this.isSprinting);
        data.putBool("grounded", this.grounded);

        if (this.color != Colors.WHITE) data.putInt("color", this.color);
        if (this.roll != 0) data.putFloat("roll", this.roll);

        boolean allZeroes = true;

        for (int i = 0; i < this.sticks.length; i++)
        {
            if (Math.abs(this.sticks[i]) > 0.01F)
            {
                allZeroes = false;

                break;
            }
        }

        if (!allZeroes)
        {
            ListType sticks = new ListType();

            for (int i = 0; i < this.sticks.length; i++)
            {
                sticks.addFloat(this.sticks[i]);
            }

            data.put("sticks", sticks);
        }

        if (this.gamepad != 0)
        {
            data.putInt("gamepad", this.gamepad);
        }

        /* Actions */
        if (!this.actions.isEmpty())
        {
            ListType actionsTag = new ListType();

            for (Action action : this.actions)
            {
                actionsTag.add(BBS.getFactoryActions().toData(action));
            }

            data.put("actions", actionsTag);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        this.x = data.getDouble("x");
        this.y = data.getDouble("y");
        this.z = data.getDouble("z");

        this.vX = data.getFloat("vx");
        this.vY = data.getFloat("vy");
        this.vZ = data.getFloat("vz");

        this.yaw = data.getFloat("ry");
        this.pitch = data.getFloat("rx");
        this.bodyYaw = data.getFloat("rz");

        this.fall = data.getFloat("fall");

        this.isSneaking = data.getBool("sneaking");
        this.isSprinting = data.getBool("sprinting");
        this.grounded = data.getBool("grounded");

        if (data.has("roll")) this.roll = data.getFloat("roll");
        if (data.has("color")) this.color = data.getInt("color");
        if (data.has("sticks"))
        {
            ListType sticks = data.getList("sticks");

            for (int i = 0; i < Math.min(sticks.size(), this.sticks.length); i++)
            {
                this.sticks[i] = sticks.getFloat(i);
            }
        }
        if (data.has("gamepad")) this.gamepad = data.getInt("gamepad");

        /* Actions */
        ListType actionTag = data.getList("actions");

        for (int i = 0, c = actionTag.size(); i < c; i++)
        {
            try
            {
                Action action = BBS.getFactoryActions().fromData(actionTag.getMap(i));

                if (action != null)
                {
                    this.actions.add(action);
                }
                else
                {
                    System.err.println("Action can't be parsed: " + actionTag.getMap(i));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}