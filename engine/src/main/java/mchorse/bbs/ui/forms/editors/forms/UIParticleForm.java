package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.forms.ParticleForm;

public class UIParticleForm extends UIForm<ParticleForm>
{
    public UIParticleForm()
    {
        super();

        this.registerDefaultPanels();

        this.defaultPanel = this.panels.get(this.panels.size() - 1);
    }
}