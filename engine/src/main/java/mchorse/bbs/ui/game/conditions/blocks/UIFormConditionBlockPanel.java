package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.conditions.blocks.FormConditionBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;
import mchorse.bbs.ui.game.utils.UITarget;

public class UIFormConditionBlockPanel extends UIConditionBlockPanel<FormConditionBlock>
{
    public UITarget target;
    public UINestedEdit form;
    public UIToggle onlyName;

    public UIFormConditionBlockPanel(UIConditionOverlayPanel overlay, FormConditionBlock block)
    {
        super(overlay, block);

        this.target = new UITarget(block.target).skipGlobal();

        this.form = new UINestedEdit((editing) ->
        {
            Form m = FormUtils.fromData(this.block.form);

            UIFormPalette.open(overlay.getParent(), editing, m, (form) ->
            {
                this.block.form = FormUtils.toData(form);
                this.form.setForm(form);
            });
        });

        this.form.setForm(FormUtils.fromData(this.block.form));
        this.onlyName = new UIToggle(UIKeys.CONDITIONS_FORM_ONLY_NAME, (b) -> this.block.onlyName = b.getValue());
        this.onlyName.setValue(this.block.onlyName).tooltip(UIKeys.CONDITIONS_FORM_ONLY_NAME_TOOLTIP);

        this.add(this.form, this.onlyName);
        this.add(this.target.marginTop(12));
    }
}