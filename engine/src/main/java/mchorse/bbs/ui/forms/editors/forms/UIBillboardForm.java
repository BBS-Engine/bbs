package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.BillboardForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.panels.UIBillboardFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIBillboardForm extends UIForm<BillboardForm>
{
    public UIBillboardForm()
    {
        super();

        this.defaultPanel = new UIBillboardFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_BILLBOARD_TITLE, Icons.MATERIAL);
        this.registerDefaultPanels();
    }
}