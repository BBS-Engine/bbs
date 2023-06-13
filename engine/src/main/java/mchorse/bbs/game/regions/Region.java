package mchorse.bbs.game.regions;

import mchorse.bbs.BBS;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.BBSData;
import mchorse.bbs.game.conditions.Condition;
import mchorse.bbs.game.regions.shapes.BoxShape;
import mchorse.bbs.game.regions.shapes.Shape;
import mchorse.bbs.game.states.States;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.game.utils.EnumUtils;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class Region implements IMapSerializable
{
    public boolean passable = true;
    public Condition enabled = new Condition(true);
    public int delay;
    public int update = 3;
    public Trigger onEnter = new Trigger();
    public Trigger onExit = new Trigger();
    public List<Shape> shapes = new ArrayList<Shape>();

    public Region()
    {
        this.shapes.add(new BoxShape());
    }

    /* Automatic state writing */
    public boolean writeState;
    public String state = "";
    public TargetMode target = TargetMode.GLOBAL;
    public boolean additive = true;
    public boolean once;

    public boolean isEnabled(Entity player)
    {
        if (this.once)
        {
            States states = this.getStates(player);

            if (states != null && states.values.containsKey(this.state))
            {
                return false;
            }
        }

        return this.enabled.execute(new DataContext(player));
    }

    public boolean isPlayerInside(Entity player, Vector3i block)
    {
        for (Shape shape : this.shapes)
        {
            if (shape.isPlayerInside(player, block))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isPlayerInside(double x, double y, double z, Vector3i block)
    {
        for (Shape shape : this.shapes)
        {
            if (shape.isPlayerInside(x, y, z, block))
            {
                return true;
            }
        }

        return false;
    }

    public void triggerEnter(Entity player, Vector3i block)
    {
        if (this.writeState && !this.state.isEmpty())
        {
            States states = getStates(player);

            if (this.additive)
            {
                states.add(this.state, 1);
            }
            else
            {
                states.setNumber(this.state, 1);
            }
        }

        this.onEnter.trigger(new DataContext(player)
            .set("x", block.x)
            .set("y", block.y)
            .set("z", block.z));
    }

    public void triggerExit(Entity player, Vector3i block)
    {
        if (this.writeState && !this.state.isEmpty())
        {
            States states = this.getStates(player);

            if (!this.additive)
            {
                states.reset(this.state);
            }
        }

        this.onExit.trigger(new DataContext(player)
            .set("x", block.x)
            .set("y", block.y)
            .set("z", block.z));
    }

    private States getStates(Entity player)
    {
        return this.target == TargetMode.GLOBAL ? BBSData.getStates() : EntityUtils.getStates(player);
    }

    @Override
    public void toData(MapType data)
    {
        data.putBool("passable", this.passable);
        data.put("enabled", this.enabled.toData());
        data.putInt("delay", this.delay);
        data.putInt("update", this.update);
        data.put("onEnter", this.onEnter.toData());
        data.put("onExit", this.onExit.toData());

        ListType shapes = new ListType();

        for (Shape shape : this.shapes)
        {
            shapes.add(BBS.getFactoryShapes().toData(shape));
        }

        data.put("shapes", shapes);
        data.putBool("writeState", this.writeState);
        data.putString("state", this.state.trim());
        data.putInt("target", this.target.ordinal());
        data.putBool("additive", this.additive);
        data.putBool("once", this.once);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("passable"))
        {
            this.passable = data.getBool("passable");
        }

        if (data.has("enabled"))
        {
            this.enabled.fromData(data.getMap("enabled"));
        }

        if (data.has("delay"))
        {
            this.delay = data.getInt("delay");
        }

        if (data.has("update"))
        {
            this.update = data.getInt("update");
        }

        if (data.has("onEnter"))
        {
            this.onEnter.fromData(data.getMap("onEnter"));
        }

        if (data.has("onExit"))
        {
            this.onExit.fromData(data.getMap("onExit"));
        }

        this.shapes.clear();

        if (data.has("shapes"))
        {
            ListType list = data.getList("shapes");

            for (int i = 0; i < list.size(); i++)
            {
                Shape shape = BBS.getFactoryShapes().fromData(list.getMap(i));

                if (shape != null)
                {
                    this.shapes.add(shape);
                }
            }
        }

        if (this.shapes.isEmpty())
        {
            this.shapes.add(new BoxShape());
        }

        this.writeState = data.getBool("writeState");
        this.state = data.getString("state");
        this.target = EnumUtils.getValue(data.getInt("target"), TargetMode.values(), TargetMode.GLOBAL);
        this.additive = data.getBool("additive");
        this.once = data.getBool("once");
    }
}