package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.forms.forms.ItemForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.ui.utils.UI;

public class UIItemFormPanel extends UIFormPanel<ItemForm>
{
    public UISlot stack;
    public UITrackpad slot;

    public UIItemFormPanel(UIForm<ItemForm> editor)
    {
        super(editor);

        this.stack = new UISlot(0, (b) -> this.form.stack.set(b.copy()));
        this.stack.relative(this).x(0.5F).y(1F, -10).anchor(0.5F, 1F);

        this.slot = new UITrackpad((v) -> this.form.slot.set(v.intValue()));
        this.slot.limit(-1).integer();

        this.add(this.stack);

        this.options.add(UI.label(UIKeys.FORMS_EDITORS_ITEM_SLOT), this.slot);
    }

    @Override
    public void startEdit(ItemForm form)
    {
        super.startEdit(form);

        this.stack.setStack(form.stack.get());
        this.slot.setValue(form.slot.get());
    }
}