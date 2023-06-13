package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.BlockForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.panels.UIBlockFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIBlockForm extends UIForm<BlockForm>
{
    public UIBlockForm()
    {
        super();

        this.defaultPanel = new UIBlockFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_BLOCK_TITLE, Icons.BLOCK);
        this.registerDefaultPanels();
    }
}