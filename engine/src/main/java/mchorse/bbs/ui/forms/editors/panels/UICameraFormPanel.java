package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.forms.forms.CameraForm;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;

public class UICameraFormPanel extends UIFormPanel<CameraForm>
{
    public UIToggle enabled;
    public UITextbox texture;
    public UITrackpad width;
    public UITrackpad height;

    public UICameraFormPanel(UIForm editor)
    {
        super(editor);

        this.enabled = new UIToggle(UIKeys.FORMS_EDITORS_CAMERA_ENABLED, (b) -> this.form.enabled.set(b.getValue()));
        this.texture = new UITextbox(200, (t) -> this.form.texture.set(t.isEmpty() ? null : Link.create(t))).delayedInput();
        this.width = new UITrackpad((v) -> this.form.width.set(v.intValue())).limit(1, 4096).integer();
        this.height = new UITrackpad((v) -> this.form.height.set(v.intValue())).limit(1, 4096).integer();

        this.options.add(this.enabled);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_CAMERA_TEXTURE).marginTop(8), this.texture, UI.row(this.width, this.height));
    }

    @Override
    public void startEdit(CameraForm form)
    {
        super.startEdit(form);

        Link texture = form.texture.get();

        this.enabled.setValue(form.enabled.get());
        this.texture.setText(texture == null ? "" : texture.toString());
        this.width.setValue(form.width.get());
        this.height.setValue(form.height.get());
    }
}