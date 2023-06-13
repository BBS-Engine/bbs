package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.ItemForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.panels.UIItemFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIItemForm extends UIForm<ItemForm>
{
    public UIItemForm()
    {
        super();

        this.defaultPanel = new UIItemFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_ITEM_TITLE, Icons.CUP);
        this.registerDefaultPanels();
    }
}