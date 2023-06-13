package mchorse.bbs.ui.game.nodes.dialogues;

import mchorse.bbs.game.dialogues.nodes.QuestChainNode;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;

public class UIQuestChainNodePanel extends UIEventBaseNodePanel<QuestChainNode>
{
    public UIButton questChain;
    public UITextbox subject;

    public UIQuestChainNodePanel()
    {
        super();

        this.questChain = new UIButton(UIKeys.OVERLAYS_CHAIN, (b) -> this.openQuestChains());
        this.subject = new UITextbox(1000, (t) -> this.node.subject = t);

        this.add(this.questChain);
        this.add(UI.label(UIKeys.NODES_EVENT_SUBJECT).marginTop(12), this.subject);
    }

    private void openQuestChains()
    {
        UIDataUtils.openPicker(this.getContext(), ContentType.CHAINS, this.node.chain, (name) -> this.node.chain = name);
    }

    @Override
    public void set(QuestChainNode node)
    {
        super.set(node);

        this.subject.setText(node.subject);
    }
}