package mchorse.bbs.game.dialogues;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.utils.factory.IFactory;
import mchorse.bbs.game.utils.nodes.NodeSystem;

public class Dialogue extends NodeSystem<EventBaseNode, DialogueFactoryData>
{
    public boolean closable = true;
    public Trigger onClose = new Trigger();

    public Dialogue(IFactory<EventBaseNode, DialogueFactoryData> factory)
    {
        super(factory);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putBool("closable", this.closable);
        data.put("onClose", this.onClose.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("closable"))
        {
            this.closable = data.getBool("closable");
        }

        if (data.has("onClose"))
        {
            this.onClose.fromData(data.getMap("onClose"));
        }
    }
}