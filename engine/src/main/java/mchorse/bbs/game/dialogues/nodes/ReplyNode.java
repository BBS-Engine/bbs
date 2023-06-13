package mchorse.bbs.game.dialogues.nodes;

import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.events.EventContext;

public class ReplyNode extends DialogueNode
{
    public ReplyNode()
    {}

    public ReplyNode(String message)
    {
        this.message.text = message;
    }

    @Override
    public int execute(EventContext context)
    {
        if (context instanceof DialogueContext)
        {
            ((DialogueContext) context).addReply(this);
        }

        return -1;
    }
}