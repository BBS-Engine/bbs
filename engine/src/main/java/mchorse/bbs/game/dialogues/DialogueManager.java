package mchorse.bbs.game.dialogues;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.crafting.CraftingTable;
import mchorse.bbs.game.dialogues.nodes.CraftingNode;
import mchorse.bbs.game.dialogues.nodes.QuestChainNode;
import mchorse.bbs.game.dialogues.nodes.QuestNode;
import mchorse.bbs.game.dialogues.nodes.ReactionNode;
import mchorse.bbs.game.dialogues.nodes.ReplyNode;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.game.quests.Quest;
import mchorse.bbs.game.quests.chains.QuestInfo;
import mchorse.bbs.game.quests.chains.QuestStatus;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.manager.BaseManager;
import mchorse.bbs.world.entities.Entity;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class DialogueManager extends BaseManager<Dialogue>
{
    public static final int MAX_EXECUTIONS = 10000;

    public IBridge bridge;

    public DialogueManager(File folder, IBridge bridge)
    {
        super(folder);

        this.bridge = bridge;
    }

    @Override
    protected Dialogue createData(String id, MapType data)
    {
        Dialogue dialogue = new Dialogue(BBS.getFactoryDialogues());

        if (data != null)
        {
            dialogue.fromData(data);
        }

        return dialogue;
    }

    public void open(DataContext context, String id)
    {
        Entity player = context.getPlayer();

        if (player == null)
        {
            return;
        }

        Dialogue dialogue = this.load(id);

        if (dialogue != null)
        {
            this.open(player, dialogue, new DialogueContext(context));
        }
    }

    public void open(Entity player, Dialogue dialogue, DialogueContext context)
    {
        PlayerComponent character = player.get(PlayerComponent.class);

        if (character != null)
        {
            this.execute(dialogue, context);

            character.setDialogue(dialogue, context);

            if (context.reactionNode != null)
            {
                this.handleContext(player, dialogue, context, null);
            }
        }
    }

    public void handleContext(Entity player, Dialogue dialogue, DialogueContext context, ReactionNode last)
    {
        DialogueFragment reaction = context.reactionNode == null ? new DialogueFragment() : context.reactionNode.message.copy();

        if (last != null && last.sound != null)
        {
            BBS.getSounds().stop(last.sound);
        }

        reaction.process(context.data);

        DialogueInteraction interaction = new DialogueInteraction(dialogue.closable, reaction);
        PlayerComponent character = player.get(PlayerComponent.class);

        if (context.reactionNode != null)
        {
            interaction.setForm(context.reactionNode.form);
        }

        if (context.containsDialogueRepliesOnly())
        {
            List<DialogueFragment> replies = context.replyNodes.stream().map((r) -> ((ReplyNode) r).message.copy().process(context.data)).collect(Collectors.toList());

            interaction.addReplies(replies);
        }
        else if (!context.replyNodes.isEmpty())
        {
            for (EventBaseNode reply : context.replyNodes)
            {
                if (reply instanceof QuestNode)
                {
                    QuestNode node = (QuestNode) reply;
                    Quest quest = BBSData.getQuests().load(node.quest);

                    if (quest != null)
                    {
                        QuestStatus status = QuestStatus.AVAILABLE;

                        if (character.quests.has(quest.getId()))
                        {
                            Quest playerQuest = character.quests.getByName(quest.getId());

                            status = playerQuest.isComplete(player) ? QuestStatus.COMPLETED : QuestStatus.UNAVAILABLE;
                        }

                        interaction.addQuest(new QuestInfo(quest, status));
                    }
                }
                else if (reply instanceof QuestChainNode)
                {
                    QuestChainNode node = (QuestChainNode) reply;

                    interaction.addQuest(BBSData.getChains().evaluate(node.chain, player, context.data.process(node.subject)));
                }
                else if (reply instanceof CraftingNode)
                {
                    CraftingNode node = (CraftingNode) reply;
                    CraftingTable table = BBSData.getCrafting().load(node.table);

                    if (table != null)
                    {
                        table.filter(player);

                        interaction.addCraftingTable(table);
                    }
                }
            }
        }

        /* Write read marker of the dialogue */
        if (context.reactionNode != null)
        {
            if (context.reactionNode.read)
            {
                character.states.readDialogue(dialogue.getId(), context.reactionNode.marker);
            }

            if (context.reactionNode.sound != null)
            {
                BBS.getSounds().play(context.reactionNode.sound);
            }
        }

        interaction.open(this.bridge);
    }

    /* Dialogue execution */

    public DialogueContext execute(Dialogue event, DialogueContext context)
    {
        if (event.main != null)
        {
            this.recursiveExecute(event, event.main, context, false);
        }

        return context;
    }

    public void recursiveExecute(Dialogue system, EventBaseNode node, DialogueContext context, boolean skipFirst)
    {
        if (context.executions >= MAX_EXECUTIONS)
        {
            return;
        }

        int result = skipFirst ? EventBaseNode.ALL : node.execute(context);

        if (result >= EventBaseNode.ALL)
        {
            context.nesting += 1;

            List<EventBaseNode> children = system.getChildren(node);

            if (result == EventBaseNode.ALL)
            {
                for (EventBaseNode child : children)
                {
                    this.recursiveExecute(system, child, context, false);
                }
            }
            else if (result <= children.size())
            {
                this.recursiveExecute(system, children.get(result - 1), context, false);
            }

            context.nesting -= 1;
        }

        context.executions += 1;
    }

    public void pickReply(IBridge bridge, int index)
    {
        Entity player = bridge.get(IBridgePlayer.class).getController();
        PlayerComponent character = player.get(PlayerComponent.class);

        if (character != null && character.getDialogue() != null)
        {
            Dialogue dialogue = character.getDialogue();
            DialogueContext context = character.getDialogueContext();
            EventBaseNode node = context.replyNodes.isEmpty() ? null : context.replyNodes.get(0);

            if (index >= 0 && index < context.replyNodes.size())
            {
                node = context.replyNodes.get(index);
            }

            ReactionNode reactionNode = context.reactionNode;

            context.resetAll();
            this.recursiveExecute(dialogue, node, context, true);
            this.handleContext(player, dialogue, context, reactionNode);
        }
    }

    public void finishDialogue(Entity player)
    {
        PlayerComponent character = player.get(PlayerComponent.class);

        if (character == null)
        {
            return;
        }

        if (character.getDialogueContext() != null)
        {
            ReactionNode node = character.getDialogueContext().reactionNode;

            if (node != null && node.sound != null)
            {
                BBS.getSounds().stop(node.sound);
            }

            character.setDialogue(null, null);
        }
    }
}