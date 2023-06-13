package mchorse.bbs.ui.game.quests.objectives;

import mchorse.bbs.game.quests.objectives.StateObjective;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.game.conditions.UICondition;

public class UIStateObjective extends UIObjective<StateObjective>
{
    public UICondition expression;

    public UIStateObjective(StateObjective objective)
    {
        super(objective);

        this.expression = new UICondition(objective.condition);
        this.expression.relative(this).y(12).w(1F);

        this.message.relative(this).y(1F).w(1F).anchorY(1F);

        this.h(69);

        this.add(this.expression, this.message);
    }

    @Override
    public IKey getMessageTooltip()
    {
        return IKey.EMPTY;
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        context.font.renderWithShadow(context.render, UIKeys.QUESTS_OBJECTIVE_STATE_EXPRESSION.get(), this.expression.area.x, this.expression.area.y - 12);
    }
}