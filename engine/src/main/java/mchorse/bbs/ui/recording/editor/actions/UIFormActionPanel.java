package mchorse.bbs.ui.recording.editor.actions;

import mchorse.bbs.recording.actions.FormAction;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.utils.UICameraUtils;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;

public class UIFormActionPanel extends UIActionPanel<FormAction>
{
    public UINestedEdit form;
    public UIToggle tween;
    public UITrackpad duration;
    public UIButton interpolation;

    public UIFormActionPanel()
    {
        super();

        this.form = new UINestedEdit((editing) ->
        {
            UIFormPalette.open(this.getParentContainer(), editing, this.action.form, (form) ->
            {
                this.action.form = form;
                this.form.setForm(form);
            }).updatable();
        });

        this.tween = new UIToggle(UIKeys.RECORD_EDITOR_ACTIONS_FORM_TWEEN, (v) -> this.action.tween = v.getValue());
        this.duration = new UITrackpad((v) -> this.action.duration = v.intValue());
        this.duration.limit(0).integer();
        this.interpolation = new UIButton(UIKeys.RECORD_EDITOR_ACTIONS_FORM_INTERPOLATION, (b) ->
        {
            UICameraUtils.interps(this.getContext(), this.action.interpolation, (i) ->
            {
                this.action.interpolation = i;
            });
        });

        this.add(UI.label(UIKeys.RECORD_EDITOR_ACTIONS_FORM_FORM), this.form);
        this.add(this.tween);
        this.add(UI.label(UIKeys.RECORD_EDITOR_ACTIONS_FORM_DURATION), this.duration);
        this.add(this.interpolation);
    }

    @Override
    public void fill(FormAction action)
    {
        super.fill(action);

        this.form.setForm(action.form);
        this.tween.setValue(action.tween);
        this.duration.setValue(action.duration);
    }
}