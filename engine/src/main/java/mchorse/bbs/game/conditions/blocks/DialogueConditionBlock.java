package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;

public class DialogueConditionBlock extends TargetConditionBlock
{
    public String marker = "";

    @Override
    public boolean evaluateBlock(DataContext context)
    {
        if (this.target.mode != TargetMode.GLOBAL)
        {
            PlayerComponent character = this.target.getCharacter(context);

            return character != null && character.states.hasReadDialogue(this.id, this.marker);
        }

        return false;
    }

    @Override
    protected TargetMode getDefaultTarget()
    {
        return TargetMode.SUBJECT;
    }

    @Override
    public String stringify()
    {
        return UIKeys.CONDITIONS_DIALOGUE_WAS_READ.formatString(this.id);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putString("marker", this.marker);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.marker = data.getString("marker");
    }
}