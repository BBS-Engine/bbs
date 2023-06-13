package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.panels.UIActionsFormPanel;
import mchorse.bbs.ui.forms.editors.panels.UIModelFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIModelForm extends UIForm<ModelForm>
{
    public UIModelForm()
    {
        this.defaultPanel = new UIModelFormPanel(this);

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_MODEL_POSE, Icons.POSE);
        this.registerPanel(new UIActionsFormPanel(this), UIKeys.FORMS_EDITORS_ACTIONS_TITLE, Icons.MORE);
        this.registerDefaultPanels();
    }

    @Override
    public void finishEdit()
    {
        super.finishEdit();

        this.form.resetAnimator();
    }
}