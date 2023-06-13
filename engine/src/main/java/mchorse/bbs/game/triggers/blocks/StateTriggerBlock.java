package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.states.States;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EnumUtils;
import mchorse.bbs.game.utils.Target;
import mchorse.bbs.game.utils.TargetMode;

public class StateTriggerBlock extends StringTriggerBlock
{
    public Target target = new Target(TargetMode.GLOBAL);
    public StateMode mode = StateMode.SET;
    public Object value = 0D;

    @Override
    public void trigger(DataContext context)
    {
        States states = this.target.getStates(context);

        if (states == null)
        {
            return;
        }

        if (this.mode == StateMode.ADD && this.value instanceof Number)
        {
            states.add(this.id, ((Number) this.value).doubleValue());
        }
        else if (this.mode == StateMode.SET)
        {
            if (this.value instanceof Number)
            {
                states.setNumber(this.id, ((Number) this.value).doubleValue());
            }
            else if (this.value instanceof String)
            {
                states.setString(this.id, (String) this.value);
            }
        }
        else
        {
            states.resetMasked(this.id);
        }
    }

    @Override
    protected String getKey()
    {
        return "state";
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("target", this.target.toData());
        data.putInt("mode", this.mode.ordinal());

        if (this.value instanceof Number)
        {
            data.putDouble("value", ((Number) this.value).doubleValue());
        }
        else if (this.value instanceof String)
        {
            data.putString("value", (String) this.value);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.target.fromData(data.getMap("target"));
        this.mode = EnumUtils.getValue(data.getInt("mode"), StateMode.values(), StateMode.SET);

        if (data.has("value", BaseType.TYPE_DOUBLE))
        {
            this.value = data.getDouble("value");
        }
        else if (data.has("value", BaseType.TYPE_STRING))
        {
            this.value = data.getString("value");
        }
    }

    public static enum StateMode
    {
        ADD, SET, REMOVE
    }
}