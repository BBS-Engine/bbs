package mchorse.bbs.forms.categories;

import mchorse.bbs.BBSData;
import mchorse.bbs.forms.forms.ParticleForm;
import mchorse.bbs.ui.UIKeys;

public class ParticleFormCategory extends FormCategory
{
    public ParticleFormCategory()
    {
        super(UIKeys.FORMS_CATEGORIES_PARTICLES);
    }

    @Override
    public void update()
    {
        super.update();

        this.forms.clear();

        for (String key : BBSData.getParticles().getKeys())
        {
            ParticleForm form = new ParticleForm();

            form.setEffect(key);
            this.forms.add(form);
        }
    }
}