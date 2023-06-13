package mchorse.bbs.ui.game.nodes;

import mchorse.bbs.game.dialogues.DialogueFactoryData;
import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.game.utils.factory.IFactory;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

public class UIDialogueNodeGraph extends UINodeGraph<EventBaseNode, DialogueFactoryData>
{
    public UIDialogueNodeGraph(IFactory<EventBaseNode, DialogueFactoryData> factory, Consumer<EventBaseNode> callback)
    {
        super(factory, callback);
    }

    @Override
    protected int getColor(Link type)
    {
        return this.system.getFactory().getData(type).color;
    }

    @Override
    protected int getColor(EventBaseNode node)
    {
        return this.system.getFactory().getData(node).color;
    }

    @Override
    protected int getIndexLabelColor(EventBaseNode lastSelected, int i)
    {
        return lastSelected.binary && i >= 2 ? Colors.GRAY : Colors.WHITE;
    }

    @Override
    protected int getNodeActiveColor(EventBaseNode output, int r)
    {
        if (output.binary)
        {
            return r == 0 ? Colors.POSITIVE : (r == 1 ? Colors.NEGATIVE : Colors.INACTIVE);
        }

        return super.getNodeActiveColor(output, r);
    }

    @Override
    protected float getNodeActiveColorOpacity(EventBaseNode output, int r)
    {
        if (output.binary && r >= 2)
        {
            return 0.25F;
        }

        return super.getNodeActiveColorOpacity(output, r);
    }
}