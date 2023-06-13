package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.game.states.States;
import mchorse.bbs.game.utils.DataContext;

public class StateConditionBlock extends PropertyConditionBlock
{
    public StateConditionBlock()
    {}

    @Override
    public boolean evaluateBlock(DataContext context)
    {
        States states = this.target.getStates(context);

        if (states == null)
        {
            return false;
        }

        return this.compare(states.getNumber(this.id));
    }

    @Override
    public String stringify()
    {
        return this.comparison.stringify(this.id);
    }
}