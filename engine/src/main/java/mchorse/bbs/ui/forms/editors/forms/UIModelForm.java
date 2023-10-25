package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.panels.UIActionsFormPanel;
import mchorse.bbs.ui.forms.editors.panels.UIModelFormPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.StringUtils;
import org.joml.Matrix4f;

public class UIModelForm extends UIForm<ModelForm>
{
    public UIModelFormPanel modelPanel;

    public UIModelForm()
    {
        this.modelPanel = new UIModelFormPanel(this);
        this.defaultPanel = this.modelPanel;

        this.registerPanel(this.defaultPanel, UIKeys.FORMS_EDITORS_MODEL_POSE, Icons.POSE);
        this.registerPanel(new UIActionsFormPanel(this), UIKeys.FORMS_EDITORS_ACTIONS_TITLE, Icons.MORE);
        this.registerDefaultPanels();
    }

    @Override
    public Matrix4f getOrigin(float transition)
    {
        String path = FormUtils.getPath(this.form);

        return this.getOrigin(transition, StringUtils.combinePaths(path, this.modelPanel.groups.getCurrentFirst()));
    }

    @Override
    public void finishEdit()
    {
        super.finishEdit();

        this.form.resetAnimator();
    }
}