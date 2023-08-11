package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.ExtrudedForm;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.forms.editors.panels.UIExtrudedFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIExtrudedForm extends UIForm<ExtrudedForm>
{
    public UIExtrudedForm()
    {
        super();

        this.defaultPanel = new UIExtrudedFormPanel(this);

        this.registerPanel(this.defaultPanel, IKey.lazy("Extruded"), Icons.MATERIAL);
        this.registerDefaultPanels();
    }
}