package mchorse.bbs.ui.game.quests.objectives;

import mchorse.bbs.game.quests.objectives.Objective;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;

public abstract class UIObjective <T extends Objective> extends UIElement
{
    public UITextbox message;

    public T objective;

    public UIObjective(T objective)
    {
        super();

        this.objective = objective;

        this.message = new UITextbox(1000, (t) -> this.objective.message = t);
        this.message.tooltip(IKey.str("%s %s").format(UIKeys.QUESTS_OBJECTIVES_MESSAGE_TOOLTIP, this.getMessageTooltip()));
        this.message.setText(objective.message);
    }

    public abstract IKey getMessageTooltip();

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.message.hasParent())
        {
            context.batcher.textShadow(UIKeys.QUESTS_OBJECTIVES_MESSAGE.get(), this.message.area.x, this.message.area.y - 12);
        }
    }
}
