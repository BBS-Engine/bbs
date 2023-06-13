package mchorse.bbs.game.dialogues;

import mchorse.bbs.game.dialogues.nodes.QuestChainNode;
import mchorse.bbs.game.dialogues.nodes.QuestNode;
import mchorse.bbs.game.dialogues.nodes.ReactionNode;
import mchorse.bbs.game.dialogues.nodes.ReplyNode;
import mchorse.bbs.game.events.EventContext;
import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.game.utils.DataContext;

import java.util.ArrayList;
import java.util.List;

public class DialogueContext extends EventContext
{
    public ReactionNode reactionNode;
    public List<EventBaseNode> replyNodes = new ArrayList<EventBaseNode>();

    public DialogueContext(DataContext data)
    {
        super(data);
    }

    public void resetAll()
    {
        this.reactionNode = null;
        this.replyNodes.clear();
    }

    public boolean containsDialogueRepliesOnly()
    {
        return this.replyNodes.size() >= 1 && this.replyNodes.get(0) instanceof ReplyNode;
    }

    public <T> T getFirstReplyNodeAs(Class<T> clazz)
    {
        if (this.replyNodes.isEmpty())
        {
            return null;
        }

        EventBaseNode node = this.replyNodes.get(0);

        return clazz.isInstance(node) ? clazz.cast(node) : null;
    }

    public void addReply(EventBaseNode node)
    {
        if (node instanceof ReplyNode || node instanceof QuestNode || node instanceof QuestChainNode)
        {
            this.replyNodes.add(node);
        }
        else
        {
            this.replyNodes.clear();
            this.replyNodes.add(node);
        }
    }
}