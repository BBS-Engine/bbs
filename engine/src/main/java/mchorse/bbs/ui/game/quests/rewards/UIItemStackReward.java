package mchorse.bbs.ui.game.quests.rewards;

import mchorse.bbs.game.quests.rewards.ItemStackReward;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.game.utils.UIItems;

public class UIItemStackReward extends UIReward<ItemStackReward>
{
    public UIItems items;

    public UIItemStackReward(ItemStackReward reward)
    {
        super(reward);

        this.items = new UIItems(UIKeys.QUESTS_REWARD_ITEM_TITLE, reward.stacks);
        this.items.relative(this).full();
        this.flex = this.items.getFlex();

        this.add(this.items);
    }
}