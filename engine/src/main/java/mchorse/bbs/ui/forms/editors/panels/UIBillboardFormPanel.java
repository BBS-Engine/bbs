package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.forms.forms.BillboardForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.forms.editors.utils.UICropOverlayPanel;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Color;

public class UIBillboardFormPanel extends UIFormPanel<BillboardForm>
{
    public UIButton pick;
    public UIToggle billboard;

    public UIButton openCrop;
    public UIToggle resizeCrop;
    public UIColor color;

    public UITrackpad offsetX;
    public UITrackpad offsetY;
    public UITrackpad rotation;

    public UIBillboardFormPanel(UIForm editor)
    {
        super(editor);

        this.pick = new UIButton(UIKeys.FORMS_EDITORS_BILLBOARD_PICK_TEXTURE, (b) ->
        {
            UITexturePicker.open(this, this.form.texture.get(), (l) -> this.form.texture.set(l));
        });
        this.billboard = new UIToggle(UIKeys.FORMS_EDITORS_BILLBOARD_TITLE, false, (b) -> this.form.billboard.set(b.getValue()));
        this.openCrop = new UIButton(UIKeys.FORMS_EDITORS_BILLBOARD_EDIT_CROP, (b) ->
        {
            UIOverlay.addOverlay(this.getContext(), new UICropOverlayPanel(this.form.texture.get(), this.form.crop.get()), 0.5F, 0.5F);
        });
        this.resizeCrop = new UIToggle(UIKeys.FORMS_EDITORS_BILLBOARD_RESIZE_CROP, false, (b) -> this.form.resizeCrop.set(b.getValue()));
        this.color = new UIColor((value) -> this.form.color.set(Color.rgba(value))).direction(Direction.TOP).withAlpha();

        this.offsetX = new UITrackpad((value) -> this.form.offsetX.set(value.floatValue()));
        this.offsetX.tooltip(UIKeys.FORMS_EDITORS_BILLBOARD_OFFSET_X);
        this.offsetY = new UITrackpad((value) -> this.form.offsetY.set(value.floatValue()));
        this.offsetY.tooltip(UIKeys.FORMS_EDITORS_BILLBOARD_OFFSET_Y);
        this.rotation = new UITrackpad((value) -> this.form.rotation.set(value.floatValue()));
        this.rotation.tooltip(UIKeys.FORMS_EDITORS_BILLBOARD_ROTATION);

        this.options.add(this.pick, this.color, this.billboard);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_BILLBOARD_CROP).marginTop(8), this.openCrop, this.resizeCrop);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_BILLBOARD_UV_SHIFT).marginTop(8), UI.row(this.offsetX, this.offsetY), this.rotation);
    }

    @Override
    public void startEdit(BillboardForm form)
    {
        super.startEdit(form);

        this.billboard.setValue(form.billboard.get());

        this.resizeCrop.setValue(form.resizeCrop.get());

        this.color.setColor(form.color.get().getARGBColor());
        this.offsetX.setValue(form.offsetX.get());
        this.offsetY.setValue(form.offsetY.get());
    }
}