package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.triggers.blocks.FormTriggerBlock;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.game.utils.UITarget;

public class UIFormTriggerBlockPanel extends UITriggerBlockPanel<FormTriggerBlock>
{
    public UITarget target;
    public UINestedEdit form;

    public UIFormTriggerBlockPanel(UITriggerOverlayPanel overlay, FormTriggerBlock block)
    {
        super(overlay, block);

        this.target = new UITarget(block.target).skipGlobal();
        this.form = new UINestedEdit((editing) ->
        {
            Form f = FormUtils.fromData(this.block.form);

            UIFormPalette.open(overlay.getParent(), editing, f, (form) ->
            {
                this.block.form = FormUtils.toData(form);
                this.form.setForm(form);
            });
        });
        this.form.setForm(FormUtils.fromData(this.block.form));

        this.add(this.form);
        this.add(this.target.marginTop(12));
    }
}