package mchorse.bbs.ui.recording.editor.actions;

import mchorse.bbs.recording.clips.FormActionClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.IUIClipsDelegate;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.camera.utils.UICameraUtils;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.utils.UI;

public class UIFormActionPanel extends UIClip<FormActionClip>
{
    public UINestedEdit form;
    public UIToggle tween;
    public UIButton interpolation;

    public UIFormActionPanel(FormActionClip clip, IUIClipsDelegate delegate)
    {
        super(clip, delegate);

        this.form = new UINestedEdit((editing) ->
        {
            UIFormPalette.open(this.getParentContainer(), editing, this.clip.form.get(), (form) ->
            {
                this.editor.postUndo(this.editor.createUndo(this.clip.form, (value) -> value.set(form)));
                this.form.setForm(form);
            }).updatable();
        });

        this.tween = new UIToggle(UIKeys.RECORD_EDITOR_ACTIONS_FORM_TWEEN, (b) ->
        {
            this.editor.postUndo(this.editor.createUndo(this.clip.tween, (value) -> value.set(b.getValue())));
        });
        this.interpolation = new UIButton(UIKeys.RECORD_EDITOR_ACTIONS_FORM_INTERPOLATION, (b) ->
        {
            UICameraUtils.interps(this.getContext(), this.clip.interpolation.get(), (i) ->
            {
                this.editor.postUndo(this.editor.createUndo(this.clip.interpolation, (value) -> value.set(i)));
            });
        });

        this.add(UI.label(UIKeys.RECORD_EDITOR_ACTIONS_FORM_FORM), this.form);
        this.add(this.tween);
        this.add(this.interpolation);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.form.setForm(this.clip.form.get());
        this.tween.setValue(this.clip.tween.get());
    }
}