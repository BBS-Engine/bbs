package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.forms.forms.ExtrudedForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;

public class UIExtrudedFormPanel extends UIFormPanel<ExtrudedForm>
{
    public UIButton pick;

    public UIExtrudedFormPanel(UIForm editor)
    {
        super(editor);

        this.pick = new UIButton(UIKeys.FORMS_EDITORS_BILLBOARD_PICK_TEXTURE, (b) ->
        {
            UITexturePicker.open(this, this.form.texture.get(), (l) -> this.form.texture.set(l));
        });
        this.options.add(this.pick);
    }
}