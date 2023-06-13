package mchorse.bbs.game.events;

import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.nodes.NodeSystem;

public class EventContext
{
    public NodeSystem<EventBaseNode, Object> system;

    public DataContext data;

    public int nesting = 0;
    public int executions = 0;

    public EventContext(DataContext data)
    {
        this.data = data;
    }
}