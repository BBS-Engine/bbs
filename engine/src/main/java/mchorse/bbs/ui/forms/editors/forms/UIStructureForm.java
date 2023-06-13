package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.StructureForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.panels.UIStructureFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIStructureForm extends UIForm<StructureForm>
{
    public UIStructureForm()
    {
        super();

        this.defaultPanel = new UIStructureFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_STRUCTURE_TITLE, Icons.STRUCTURE);
        this.registerDefaultPanels();
    }
}