package mchorse.bbs.game.events.nodes;

import mchorse.bbs.game.events.EventContext;

public class CancelNode extends EventBaseNode
{
    @Override
    public int execute(EventContext context)
    {
        context.data.cancel();

        return EventBaseNode.HALT;
    }
}