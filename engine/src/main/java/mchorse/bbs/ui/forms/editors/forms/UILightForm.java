package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.LightForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.panels.UILightFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UILightForm extends UIForm<LightForm>
{
    public UILightForm()
    {
        super();

        this.defaultPanel = new UILightFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_LIGHT_TITLE, Icons.LIGHT);
        this.registerDefaultPanels();
    }
}