package mchorse.bbs.recording.actions;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

import java.util.Objects;

public class FormAction extends Action
{
    public Form form;
    public boolean tween = true;
    public int duration = 10;
    public Interpolation interpolation = Interpolation.LINEAR;

    @Override
    public void apply(Entity actor)
    {
        super.apply(actor);

        FormComponent component = actor.get(FormComponent.class);

        if (component != null && !Objects.equals(component.form, this.form))
        {
            Form copy = FormUtils.copy(this.form);

            if (this.tween && component.form != null && copy != null)
            {
                component.form.tween(copy, this.duration, this.interpolation);
            }
            else
            {
                component.form = copy;
            }
        }
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("form"))
        {
            this.form = FormUtils.fromData(data.getMap("form"));
        }

        this.tween = data.getBool("tween", this.tween);
        this.duration = data.getInt("duration", this.duration);

        try
        {
            this.interpolation = Interpolation.valueOf(data.getString("interp"));
        }
        catch (Exception e)
        {
            this.interpolation = Interpolation.LINEAR;
        }
    }

    @Override
    public void toData(MapType data)
    {
        if (this.form != null)
        {
            data.put("form", FormUtils.toData(this.form));
        }

        data.putBool("tween", this.tween);
        data.putInt("duration", this.duration);
        data.putString("interp", this.interpolation.toString());
    }
}