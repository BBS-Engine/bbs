package mchorse.bbs.ui.game.interaction;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.game.crafting.CraftingTable;
import mchorse.bbs.game.dialogues.DialogueFragment;
import mchorse.bbs.game.dialogues.DialogueInteraction;
import mchorse.bbs.game.quests.chains.QuestInfo;
import mchorse.bbs.game.quests.chains.QuestStatus;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.utils.UIFormRenderer;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.utils.UIClickableText;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.game.crafting.ICraftingScreen;
import mchorse.bbs.ui.game.crafting.UICrafting;
import mchorse.bbs.ui.game.quests.UIQuestCard;
import mchorse.bbs.ui.game.quests.UIQuestInfoList;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;

public abstract class UIBaseInteractionMenu extends UIBaseMenu implements ICraftingScreen, IUIInteractionMenu
{
    public UIFormRenderer form;
    public UIButton back;

    /* Dialogue */
    public UIText reactionText;
    public UIScrollView replies;

    /* Crafting */
    public UICrafting crafting;

    /* Quests */
    public UIElement quest;
    public UIQuestInfoList quests;
    public UIScrollView questArea;
    public UIButton accept;

    protected DialogueInteraction interaction;

    public UIBaseInteractionMenu(IBridge bridge)
    {
        super(bridge);
    }

    @Override
    public Link getMenuId()
    {
        return Link.bbs("dialogue");
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    /* Dialogue */

    protected void updateVisibility()
    {
        this.back.setVisible(!this.interaction.quests.isEmpty() || this.interaction.table != null);
        this.quest.setVisible(!this.interaction.quests.isEmpty());
        this.accept.setVisible(!this.interaction.quests.isEmpty());
        this.crafting.setVisible(this.interaction.table != null);
        this.replies.setVisible(this.interaction.quests.isEmpty() && this.interaction.table == null);
    }

    private void setInteraction(DialogueInteraction fragment)
    {
        if (fragment.form != null)
        {
            this.form.form = fragment.form;
        }

        this.interaction = fragment;

        this.reactionText.text(fragment.reaction.getProcessedText());
        this.reactionText.color(fragment.reaction.color, true);
        this.replies.removeAll();

        for (DialogueFragment reply : fragment.replies)
        {
            UIClickableText replyElement = new UIClickableText();

            replyElement.callback(this::pickReply);
            replyElement.color(reply.color, true);
            replyElement.hoverColor(Colors.mulRGB(reply.color, 0.7F));
            this.replies.add(replyElement.text("> " + reply.getProcessedText()));
        }

        this.updateVisibility();
        this.main.resize();
    }

    private void pickReply(UIClickableText text)
    {
        BBSData.getDialogues().pickReply(this.bridge, this.replies.getChildren().indexOf(text));
    }

    @Override
    public void pickReply(DialogueInteraction fragment)
    {
        this.setInteraction(fragment);

        if (fragment.reaction.text.isEmpty() && fragment.isEmpty())
        {
            this.closeMenu();

            return;
        }

        if (fragment.hasQuests())
        {
            this.setQuests(fragment.quests);
        }
        else if (fragment.table != null)
        {
            this.setCraftingTable(fragment.table);
        }
    }

    /* Crafting */

    public void setCraftingTable(CraftingTable table)
    {
        this.crafting.set(table);

        this.updateVisibility();
        this.main.resize();
    }

    @Override
    public void refresh()
    {
        this.crafting.refresh();
    }

    /* Quests */

    public void setQuests(List<QuestInfo> quests)
    {
        this.quests.clear();
        this.quests.setList(quests);
        this.quests.sort();

        this.quests.setVisible(!quests.isEmpty());
        this.quests.setIndex(0);
        this.pickQuest(this.quests.getCurrentFirst());

        this.updateVisibility();
        this.main.resize();
    }

    public void pickQuest(QuestInfo info)
    {
        this.questArea.removeAll();
        this.accept.label = info != null && info.status == QuestStatus.COMPLETED
            ? UIKeys.INTERACTION_COMPLETE
            : UIKeys.INTERACTION_ACCEPT;

        if (info != null)
        {
            UIQuestCard.fillQuest(this.bridge.get(IBridgePlayer.class).getController(), this.questArea, info.quest, true);
        }

        this.questArea.resize();
        this.accept.setEnabled(info != null && info.status != QuestStatus.UNAVAILABLE);
    }

    public void actionQuest()
    {
        QuestInfo info = this.quests.getCurrentFirst();

        BBSData.getQuests().performQuestAction(this.bridge, info.quest.getId(), info.status);

        if (this.interaction.isSingleQuest() && info.status == QuestStatus.COMPLETED)
        {
            this.back.clickItself();

            return;
        }

        if (info.status == QuestStatus.AVAILABLE)
        {
            info.status = QuestStatus.UNAVAILABLE;

            this.accept.setEnabled(false);
        }
        else if (info.status == QuestStatus.COMPLETED)
        {
            this.quests.setIndex(0);

            info = this.quests.getCurrentFirst();

            this.pickQuest(info);
        }
    }

    @Override
    public void update()
    {
        super.update();

        if (this.form.form != null)
        {
            this.form.form.update(this.form.getEntity());
        }
    }

    @Override
    protected void closeMenu()
    {
        if (this.interaction.closable || this.interaction.isEmpty())
        {
            super.closeMenu();

            BBSData.getDialogues().finishDialogue(this.bridge.get(IBridgePlayer.class).getController());
        }
    }

    @Override
    public void renderMenu(UIRenderingContext context, int mouseX, int mouseY)
    {
        super.renderMenu(context, mouseX, mouseY);

        if (this.interaction.isEmpty())
        {
            this.context.font.renderCentered(context, UIKeys.INTERACTION_NO_REPLIES.get(), this.replies.area.mx(), this.replies.area.my(), 0x333333);
        }

        if (!this.interaction.quests.isEmpty() && this.quests.getList().isEmpty())
        {
            int w = (int) (this.questArea.area.w / 1.5F);

            context.draw.wallText(this.context.font, UIKeys.INTERACTION_NO_QUESTS.get(), this.questArea.area.mx() - w / 2, (this.quest.area.y + this.accept.area.y - 10) / 2, Colors.WHITE, w, 12, 0.5F, 0.5F);
        }
    }
}