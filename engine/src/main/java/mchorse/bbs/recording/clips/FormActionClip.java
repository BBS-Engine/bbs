package mchorse.bbs.recording.clips;

import mchorse.bbs.camera.values.ValueInterpolation;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.recording.values.ValueForm;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

import java.util.Objects;

public class FormActionClip extends ActionClip
{
    public final ValueForm form = new ValueForm("form");
    public final ValueBoolean tween = new ValueBoolean("tween", true);
    public final ValueInterpolation interpolation = new ValueInterpolation("interpolation");

    public FormActionClip()
    {
        this.register(this.form);
        this.register(this.tween);
        this.register(this.interpolation);
    }

    @Override
    public void apply(Entity actor, int offset, boolean playing)
    {
        super.apply(actor, offset, playing);

        FormComponent component = actor.get(FormComponent.class);

        if (component != null)
        {
            Form form = this.form.get();

            if (!playing || !Objects.equals(component.form, form))
            {
                if (this.tween.get() && component.form != null && form != null)
                {
                    component.form.tween(form, this.duration.get(), this.interpolation.get(), offset, playing);
                }
                else
                {
                    component.form = form;
                }
            }
        }
    }

    @Override
    protected Clip create()
    {
        return new FormActionClip();
    }
}