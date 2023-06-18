package mchorse.sandbox.ui.player;

import mchorse.sandbox.Sandbox;
import mchorse.sandbox.ui.inventory.UIInventoryHandler;
import mchorse.sandbox.ui.inventory.UIPlayerInventory;
import mchorse.sandbox.ui.inventory.UIPlayerSlot;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.quests.Quest;
import mchorse.bbs.game.quests.chains.QuestInfo;
import mchorse.bbs.game.quests.chains.QuestStatus;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.forms.editors.utils.UIFormRenderer;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.game.quests.UIQuestCard;
import mchorse.bbs.ui.game.quests.UIQuestInfoList;

import java.util.ArrayList;
import java.util.List;

public abstract class UIBasePlayerMenu extends UIBaseMenu
{
    public UIFormRenderer form;
    public UIInventoryHandler handler;
    public UIPlayerInventory inventory;
    public UIPlayerSlot mainHand;
    public UIPlayerSlot offHand;

    public UIQuestInfoList quests;
    public UIScrollView quest;

    public UIBasePlayerMenu(IBridge bridge)
    {
        super(bridge);
    }

    @Override
    public Link getMenuId()
    {
        return Sandbox.link("player");
    }

    protected List<QuestInfo> collectQuestInfos(PlayerComponent character)
    {
        List<QuestInfo> questInfos = new ArrayList<QuestInfo>();

        for (Quest quest : character.quests.quests.values())
        {
            questInfos.add(new QuestInfo(quest, quest.isComplete(character.getEntity()) ? QuestStatus.COMPLETED : QuestStatus.AVAILABLE));
        }

        return questInfos;
    }

    protected void pickQuest(QuestInfo questInfo)
    {
        this.quest.removeAll();

        UIQuestCard.fillQuest(this.bridge.get(IBridgePlayer.class).getController(), this.quest, questInfo.quest, true);

        this.quest.resize();
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    @Override
    protected void closeMenu()
    {
        super.closeMenu();

        this.handler.close();
    }
}