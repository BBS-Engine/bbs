package mchorse.bbs.ui.recording.editor.actions;

import mchorse.bbs.recording.clips.FormActionClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.UIClip;
import mchorse.bbs.ui.film.utils.UICameraUtils;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;

public class UIFormAction extends UIClip<FormActionClip>
{
    public UINestedEdit form;
    public UIToggle tween;
    public UIButton interpolation;

    public UIFormAction(FormActionClip clip, IUIClipsDelegate delegate)
    {
        super(clip, delegate);

        this.form = new UINestedEdit((editing) ->
        {
            UIFormPalette.open(this.getParentContainer(), editing, this.clip.form.get(), (form) ->
            {
                this.editor.postUndo(this.editor.createUndo(this.clip.form, (value) -> value.set(form)));
                this.clip.form.set(form);
                this.form.setForm(form);
            }).updatable();
        }).keybinds();

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

        this.panels.add(UIClip.label(UIKeys.RECORD_EDITOR_ACTIONS_FORM_FORM).marginTop(12), this.form);
        this.panels.add(this.tween);
        this.panels.add(this.interpolation);
    }

    @Override
    protected void addEnvelopes()
    {}

    @Override
    public void fillData()
    {
        super.fillData();

        this.form.setForm(this.clip.form.get());
        this.tween.setValue(this.clip.tween.get());
    }
}