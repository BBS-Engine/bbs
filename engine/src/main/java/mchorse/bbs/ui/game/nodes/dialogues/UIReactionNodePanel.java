package mchorse.bbs.ui.game.nodes.dialogues;

import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.dialogues.nodes.DialogueNode;
import mchorse.bbs.game.dialogues.nodes.ReactionNode;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UISoundOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;

public class UIReactionNodePanel extends UIDialogueNodePanel
{
    public UINestedEdit form;
    public UIIcon sound;
    public UIToggle read;
    public UITextbox marker;

    public UIReactionNodePanel()
    {
        super();

        this.form = new UINestedEdit(this::openFormMenu);
        this.sound = new UIIcon(Icons.SOUND, (b) -> this.openPickSoundOverlay());
        this.sound.tooltip(UIKeys.TRIGGER_SOUND);
        this.read = new UIToggle(UIKeys.NODES_DIALOGUE_READ, (b) -> this.get().read = b.getValue());
        this.read.h(20);
        this.marker = new UITextbox((t) -> this.get().marker = t);
        this.marker.filename().tooltip(UIKeys.NODES_DIALOGUE_MARKER_TOOLTIP, Direction.TOP);

        this.color.removeFromParent();
        this.addAfter(this.text, UI.row(0, this.color, this.sound));

        this.add(this.form);
        this.add(
            UI.label(UIKeys.NODES_DIALOGUE_MARKER).marginTop(12),
            UI.row(this.marker, this.read)
        );
    }

    private void openPickSoundOverlay()
    {
        UISoundOverlayPanel overlay = new UISoundOverlayPanel(this::setSound);

        UIOverlay.addOverlay(this.getContext(), overlay.set(this.get().sound), 0.5F, 0.9F);
    }

    private void setSound(Link sound)
    {
        this.get().sound = sound;
    }

    private void openFormMenu(boolean editing)
    {
        UIFormPalette.open(this.getParentContainer(), editing, this.get().form, this::setForm);
    }

    private void setForm(Form form)
    {
        form = FormUtils.copy(form);

        this.get().form = form;
        this.form.setForm(form);
    }

    public ReactionNode get()
    {
        return (ReactionNode) this.node;
    }

    @Override
    public void set(DialogueNode node)
    {
        super.set(node);

        this.form.setForm(this.get().form);
        this.read.setValue(this.get().read);
        this.marker.setText(this.get().marker);
    }
}