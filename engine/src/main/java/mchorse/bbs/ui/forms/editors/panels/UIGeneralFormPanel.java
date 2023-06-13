package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.objects.objects.UIPropTransforms;

public class UIGeneralFormPanel extends UIFormPanel
{
    public UITextbox name;
    public UIPropTransforms transform;

    public UIToggle hitbox;
    public UITrackpad hitboxWidth;
    public UITrackpad hitboxHeight;
    public UITrackpad hitboxSneakMultiplier;
    public UITrackpad hitboxEyeHeight;

    public UIGeneralFormPanel(UIForm editor)
    {
        super(editor);

        this.name = new UITextbox(120, (t) -> this.form.name.set(t));

        this.transform = new UIPropTransforms();
        this.transform.relative(this).x(0.5F).y(1F, -10).anchor(0.5F, 1F);

        this.hitbox = new UIToggle(UIKeys.FORMS_EDITORS_GENERAL_HITBOX, (b) -> this.form.hitbox.set(b.getValue()));
        this.hitboxWidth = new UITrackpad((v) -> this.form.hitboxWidth.set(v.floatValue()));
        this.hitboxWidth.limit(0).tooltip(UIKeys.FORMS_EDITORS_GENERAL_HITBOX_WIDTH);
        this.hitboxHeight = new UITrackpad((v) -> this.form.hitboxHeight.set(v.floatValue()));
        this.hitboxHeight.limit(0).tooltip(UIKeys.FORMS_EDITORS_GENERAL_HITBOX_HEIGHT);
        this.hitboxSneakMultiplier = new UITrackpad((v) -> this.form.hitboxSneakMultiplier.set(v.floatValue()));
        this.hitboxSneakMultiplier.limit(0, 1);
        this.hitboxEyeHeight = new UITrackpad((v) -> this.form.hitboxEyeHeight.set(v.floatValue()));
        this.hitboxEyeHeight.limit(0, 1);

        this.options.add(UI.label(UIKeys.FORMS_EDITORS_GENERAL_DISPLAY), this.name, this.transform.verticalCompact().marginTop(8));
        this.options.add(this.hitbox.marginTop(12), UI.row(this.hitboxWidth, this.hitboxHeight));
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_GENERAL_HITBOX_SNEAK_MULTIPLIER), this.hitboxSneakMultiplier);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_GENERAL_HITBOX_EYE_HEIGHT), this.hitboxEyeHeight);
    }

    @Override
    public void startEdit(Form form)
    {
        super.startEdit(form);

        this.name.setText(form.name.get());
        this.transform.setTransform(form.transform.get());

        this.hitbox.setValue(form.hitbox.get());
        this.hitboxWidth.setValue(form.hitboxWidth.get());
        this.hitboxHeight.setValue(form.hitboxHeight.get());
        this.hitboxSneakMultiplier.setValue(form.hitboxSneakMultiplier.get());
        this.hitboxEyeHeight.setValue(form.hitboxEyeHeight.get());
    }
}