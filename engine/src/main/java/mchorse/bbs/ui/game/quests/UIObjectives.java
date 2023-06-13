package mchorse.bbs.ui.game.quests;

import mchorse.bbs.BBS;
import mchorse.bbs.game.conditions.blocks.DialogueConditionBlock;
import mchorse.bbs.game.quests.objectives.CollectObjective;
import mchorse.bbs.game.quests.objectives.KillObjective;
import mchorse.bbs.game.quests.objectives.Objective;
import mchorse.bbs.game.quests.objectives.StateObjective;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.game.quests.objectives.UIObjective;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.List;

public class UIObjectives extends UIElement
{
    public List<Objective> objectives;

    public UIObjectives()
    {
        super();

        this.column(20).vertical().stretch();
    }

    public void getAdds(ContextMenuManager menu)
    {
        menu.action(Icons.ADD, UIKeys.QUESTS_OBJECTIVES_CONTEXT_ADD_KILL, () -> this.addObjective(new KillObjective(), true));
        menu.action(Icons.ADD, UIKeys.QUESTS_OBJECTIVES_CONTEXT_ADD_COLLECT, () -> this.addObjective(new CollectObjective(), true));
        menu.action(Icons.ADD, UIKeys.QUESTS_OBJECTIVES_CONTEXT_ADD_STATE, () -> this.addObjective(new StateObjective(), true));
        menu.action(Icons.ADD, UIKeys.QUESTS_OBJECTIVES_CONTEXT_ADD_DIALOGUE_READ, () -> this.addObjective(this.createDialogueReadObjective(), true));
    }

    private Objective createDialogueReadObjective()
    {
        StateObjective objective = new StateObjective();
        DialogueConditionBlock block = new DialogueConditionBlock();

        objective.condition.blocks.add(block);
        objective.message = UIKeys.QUESTS_OBJECTIVE_STATE_DIALOGUE.get();

        return objective;
    }

    private void addObjective(Objective objective, boolean add)
    {
        UIObjective element = null;

        try
        {
            element = (UIObjective) BBS.getFactoryObjectives().getData(objective).getConstructor(objective.getClass()).newInstance(objective);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (element != null)
        {
            this.add(element);

            final UIObjective finalElement = element;

            element.context((menu) -> menu.action(Icons.REMOVE, UIKeys.QUESTS_OBJECTIVES_CONTEXT_REMOVE, Colors.NEGATIVE, () -> this.removeObjective(finalElement)));

            if (add)
            {
                this.objectives.add(objective);
                this.getParentContainer().resize();
            }
        }
    }

    private void removeObjective(UIObjective element)
    {
        if (this.objectives.remove(element.objective))
        {
            element.removeFromParent();
            this.getParentContainer().resize();
        }
    }

    public void set(List<Objective> objectives)
    {
        this.objectives = objectives;

        this.removeAll();

        for (Objective objective : this.objectives)
        {
            this.addObjective(objective, false);
        }

        this.getParentContainer().resize();
    }
}