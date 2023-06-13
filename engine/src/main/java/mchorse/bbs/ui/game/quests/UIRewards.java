package mchorse.bbs.ui.game.quests;

import mchorse.bbs.BBS;
import mchorse.bbs.game.quests.rewards.ItemStackReward;
import mchorse.bbs.game.quests.rewards.Reward;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.game.quests.rewards.UIReward;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.List;

public class UIRewards extends UIElement
{
    public List<Reward> rewards;

    public UIRewards()
    {
        super();

        this.column(10).vertical().stretch();
    }

    public void getAdds(ContextMenuManager menu)
    {
        menu.action(Icons.ADD, UIKeys.QUESTS_REWARDS_CONTEXT_ADD_ITEM, () -> this.addReward(new ItemStackReward(), true));
    }

    private void addReward(Reward reward, boolean add)
    {
        UIReward element = null;

        try
        {
            element = (UIReward) BBS.getFactoryRewards().getData(reward).getConstructors()[0].newInstance(reward);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (element != null)
        {
            this.add(element);

            final UIReward finalElement = element;

            element.context((menu) -> menu.action(Icons.REMOVE, UIKeys.QUESTS_REWARDS_CONTEXT_REMOVE, Colors.NEGATIVE, () -> this.removeReward(finalElement)));

            if (add)
            {
                this.rewards.add(reward);
                this.getParentContainer().resize();
            }
        }
    }

    private void removeReward(UIReward element)
    {
        if (this.rewards.remove(element.reward))
        {
            element.removeFromParent();
            this.getParentContainer().resize();
        }
    }

    public void set(List<Reward> rewards)
    {
        this.rewards = rewards;

        this.removeAll();

        for (Reward reward : this.rewards)
        {
            this.addReward(reward, false);
        }

        this.getParentContainer().resize();
    }
}