package mchorse.bbs.game.events.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.events.EventContext;
import mchorse.bbs.game.utils.nodes.Node;

public abstract class EventBaseNode extends Node
{
    public static final int HALT = -1;
    public static final int ALL = 0;

    /**
     * When true, this node will return 1 or 2 depending on the
     * success of the node, as opposed to none or all
     */
    public boolean binary;

    /**
     * Executes this node, and depending on return value:
     * -1 (or below) = don't execute children nodes
     * 0             = execute all children nodes
     * 1 (or above)  = execute only the give node minus one
     */
    public abstract int execute(EventContext context);

    protected int booleanToExecutionCode(boolean result)
    {
        if (this.binary)
        {
            /* Based on the result execute either 1st or 2nd node */
            return result ? 1 : 2;
        }

        return result ? ALL : HALT;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (this.binary)
        {
            data.putBool("binary", this.binary);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("binary"))
        {
            this.binary = data.getBool("binary");
        }
    }
}