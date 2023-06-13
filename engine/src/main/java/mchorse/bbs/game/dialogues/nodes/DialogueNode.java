package mchorse.bbs.game.dialogues.nodes;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.dialogues.DialogueFragment;
import mchorse.bbs.game.events.nodes.EventBaseNode;

public abstract class DialogueNode extends EventBaseNode
{
    public DialogueFragment message = new DialogueFragment();

    @Override
    protected String getDisplayTitle()
    {
        return this.message.getProcessedText();
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        MapType message = this.message.toData();

        if (!message.isEmpty())
        {
            data.put("message", message);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("message"))
        {
            this.message.fromData(data.getMap("message"));
        }
    }
}