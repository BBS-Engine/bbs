package mchorse.bbs.ui.game.quests.rewards;

import mchorse.bbs.game.quests.rewards.Reward;
import mchorse.bbs.ui.framework.elements.UIElement;

public class UIReward <T extends Reward> extends UIElement
{
    public T reward;

    public UIReward(T reward)
    {
        super();

        this.reward = reward;
    }
}