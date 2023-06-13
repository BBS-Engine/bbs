package mchorse.bbs.game.dialogues.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.events.EventContext;
import mchorse.bbs.game.events.nodes.EventBaseNode;

public class QuestChainNode extends EventBaseNode
{
    public String chain = "";
    public String subject = "";

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
        return this.chain;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("chain", this.chain);
        data.putString("subject", this.subject.trim());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("chain"))
        {
            this.chain = data.getString("chain");
        }

        if (data.has("subject"))
        {
            this.subject = data.getString("subject");
        }
    }
}