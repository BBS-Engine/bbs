package mchorse.bbs.ui.game.nodes.dialogues;

import mchorse.bbs.game.dialogues.nodes.DialogueNode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.text.UITextarea;
import mchorse.bbs.ui.framework.elements.input.text.utils.TextLine;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;
import mchorse.bbs.ui.utils.UI;

public class UIDialogueNodePanel extends UIEventBaseNodePanel<DialogueNode>
{
    public UITextarea<TextLine> text;
    public UIColor color;

    public UIDialogueNodePanel()
    {
        super();

        this.text = new UITextarea<TextLine>((text) -> this.node.message.text = text);
        this.text.wrap().background().h(136);
        this.color = new UIColor((c) -> this.node.message.color = c);

        this.add(UI.label(UIKeys.NODES_DIALOGUE_CONTENT).marginTop(12), this.text, this.color);
    }

    @Override
    public void set(DialogueNode node)
    {
        super.set(node);

        this.text.setText(node.message.text);
        this.color.setColor(node.message.color);
    }
}