package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.BBSData;
import mchorse.bbs.game.dialogues.Dialogue;
import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.world.entities.Entity;

public class DialogueTriggerBlock extends DataTriggerBlock
{
    public DialogueTriggerBlock()
    {
        super();
    }

    @Override
    public void trigger(DataContext context)
    {
        if (!this.id.isEmpty())
        {
            Entity player = context.getPlayer();

            Dialogue dialogue = BBSData.getDialogues().load(this.id);

            if (dialogue != null)
            {
                BBSData.getDialogues().open(player, dialogue, new DialogueContext(this.apply(context)));
            }
        }
    }

    @Override
    protected String getKey()
    {
        return "dialogue";
    }
}