package mchorse.bbs.game.dialogues.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.events.EventContext;
import mchorse.bbs.game.events.nodes.EventBaseNode;

public class CraftingNode extends EventBaseNode
{
    public String table = "";

    @Override
    public int execute(EventContext context)
    {
        if (context instanceof DialogueContext)
        {
            ((DialogueContext) context).addReply(this);
        }

        return EventBaseNode.HALT;
    }

    @Override
    protected String getDisplayTitle()
    {
        return this.table;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("craftingTable", this.table);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("craftingTable"))
        {
            this.table = data.getString("craftingTable");
        }
    }
}