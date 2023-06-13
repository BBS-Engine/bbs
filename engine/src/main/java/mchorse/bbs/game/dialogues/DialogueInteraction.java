package mchorse.bbs.game.dialogues;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.crafting.CraftingTable;
import mchorse.bbs.game.quests.chains.QuestInfo;
import mchorse.bbs.game.scripts.ui.UserInterface;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.game.interaction.IUIInteractionMenu;
import mchorse.bbs.ui.game.interaction.UIBasicInteractionMenu;
import mchorse.bbs.ui.game.interaction.UICustomInteractionMenu;

import java.util.ArrayList;
import java.util.List;

public class DialogueInteraction
{
    public boolean closable;
    public Form form;
    public DialogueFragment reaction = new DialogueFragment();
    public List<DialogueFragment> replies = new ArrayList<DialogueFragment>();
    public CraftingTable table;
    public List<QuestInfo> quests = new ArrayList<QuestInfo>();

    public DialogueInteraction(boolean closable, DialogueFragment reaction)
    {
        this.closable = closable;
        this.reaction = reaction;
    }

    public boolean isEmpty()
    {
        return this.replies.isEmpty() && !this.hasQuests() && this.table == null;
    }

    public void addReplies(List<DialogueFragment> replies)
    {
        this.replies.addAll(replies);
    }

    public void setForm(Form form)
    {
        this.form = form;
    }

    public void addCraftingTable(CraftingTable table)
    {
        this.table = table;
    }

    public boolean hasQuests()
    {
        return !this.quests.isEmpty();
    }

    public boolean isSingleQuest()
    {
        return this.quests.size() == 1;
    }

    public void addQuest(QuestInfo questInfo)
    {
        if (questInfo != null)
        {
            this.quests.add(questInfo);
        }
    }

    public boolean open(IBridge bridge)
    {
        UIBaseMenu menu = bridge.get(IBridgeMenu.class).getCurrentMenu();

        if (menu instanceof IUIInteractionMenu)
        {
            IUIInteractionMenu interaction = (IUIInteractionMenu) menu;

            interaction.pickReply(this);

            return true;
        }
        else if (!this.reaction.text.isEmpty() || !this.isEmpty())
        {
            if (this.tryOpenDialogue(bridge))
            {
                return true;
            }

            bridge.get(IBridgeMenu.class).showMenu(new UIBasicInteractionMenu(bridge, this));

            return true;
        }

        return false;
    }

    private boolean tryOpenDialogue(IBridge bridge)
    {
        String customUI = BBSData.getSettings().dialogueUI.get();
        UserInterface ui = customUI.isEmpty() ? null : BBSData.getUIs().load(customUI);

        if (ui != null)
        {
            try
            {
                bridge.get(IBridgeMenu.class).showMenu(new UICustomInteractionMenu(bridge, this, UserInterfaceContext.create(ui, null)));

                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }
}