package mchorse.bbs.ui.game.quests;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.dialogues.DialogueFragment;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.quests.Quest;
import mchorse.bbs.game.quests.objectives.Objective;
import mchorse.bbs.game.quests.rewards.ItemStackReward;
import mchorse.bbs.game.quests.rewards.Reward;
import mchorse.bbs.graphics.text.Font;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.world.entities.Entity;

public class UIQuestCard
{
    public static void fillQuest(Entity player, UIElement element, Quest quest, boolean forceReward)
    {
        String title = quest.getProcessedTitle();

        if (BBSSettings.scriptUIDebug.get())
        {
            title += Font.FORMAT_LIGHT_GRAY + " (" + quest.getId() + ")";
        }

        element.add(UI.label(IKey.raw(title)).background().marginBottom(12));
        element.add(new UIText(DialogueFragment.process(quest.story)).color(Colors.LIGHTER_GRAY, true).marginBottom(12));
        element.add(UI.label(UIKeys.QUESTS_OBJECTIVES_TITLE));

        for (Objective objective : quest.objectives)
        {
            element.add(UI.label(IKey.raw("- " + objective.stringify(player))).color(Colors.LIGHTER_GRAY));
        }

        if (!BBSSettings.questsPreviewRewards.get() && !forceReward)
        {
            return;
        }

        if (quest.rewards.isEmpty())
        {
            return;
        }

        element.add(UI.label(UIKeys.QUESTS_REWARDS_TITLE).marginTop(12));

        for (Reward reward : quest.rewards)
        {
            if (reward instanceof ItemStackReward)
            {
                ItemStackReward stack = (ItemStackReward) reward;
                UIElement stacks = new UIElement();

                stacks.h(24).grid(5).resizes(false).width(24);

                for (ItemStack item : stack.stacks)
                {
                    UISlot slot = new UISlot(0, null);

                    slot.setEnabled(false);
                    slot.setStack(item);
                    slot.renderDisabled = false;
                    stacks.add(slot);
                }

                element.add(stacks);
            }
        }
    }
}