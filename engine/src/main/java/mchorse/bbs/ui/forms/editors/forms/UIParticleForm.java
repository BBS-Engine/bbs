package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.ParticleForm;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;

public class UIParticleForm extends UIForm<ParticleForm>
{
    public UIParticleForm()
    {
        super();

        this.registerDefaultPanels();

        this.defaultPanel = this.panels.get(this.panels.size() - 1);

        this.defaultPanel.options.prepend(new UIButton(UIKeys.FORMS_EDITORS_BILLBOARD_PICK_TEXTURE, (b) ->
        {
            Link texture = this.form.texture.get();

            if (this.form.getEmitter() != null && texture == null)
            {
                texture = this.form.getEmitter().scheme.texture;
            }

            UITexturePicker.open(this.defaultPanel, texture, (l) -> this.form.texture.set(l));
        }).marginBottom(6));
    }
}