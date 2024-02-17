package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.CameraForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.panels.UICameraFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UICameraForm extends UIForm<CameraForm>
{
    public UICameraForm()
    {
        super();

        this.defaultPanel = new UICameraFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_BILLBOARD_TITLE, Icons.CAMERA);
        this.registerDefaultPanels();
    }
}