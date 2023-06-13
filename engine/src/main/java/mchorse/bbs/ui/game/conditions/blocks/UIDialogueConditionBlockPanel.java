package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.game.conditions.blocks.DialogueConditionBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.game.utils.UITarget;
import mchorse.bbs.ui.utils.UI;

public class UIDialogueConditionBlockPanel extends UIConditionBlockPanel<DialogueConditionBlock>
{
    public UIButton id;
    public UITextbox marker;
    public UITarget target;

    public UIDialogueConditionBlockPanel(UIConditionOverlayPanel overlay, DialogueConditionBlock block)
    {
        super(overlay, block);

        this.id = new UIButton(UIKeys.OVERLAYS_DIALOGUE, (t) -> this.openDialogues());
        this.marker = new UITextbox(1000, (t) -> this.block.marker = t);
        this.marker.setText(block.marker);
        this.target = new UITarget(block.target).skipGlobal().skip(TargetMode.NPC);

        this.add(UI.row(
            UI.column(UI.label(UIKeys.CONDITIONS_DIALOGUE_ID).marginTop(12), this.id),
            UI.column(UI.label(UIKeys.CONDITIONS_DIALOGUE_MARKER).marginTop(12), this.marker)
        ));
        this.add(this.target.marginTop(12));
    }

    private void openDialogues()
    {
        UIDataUtils.openPicker(this.getContext(), ContentType.DIALOGUES, this.block.id, (name) -> this.block.id = name);
    }
}