package mchorse.bbs.forms.properties;

import mchorse.bbs.cubic.animation.ActionsConfig;
import mchorse.bbs.cubic.animation.Animator;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.utils.math.IInterpolation;

import java.util.Objects;

public class ActionsConfigProperty extends BaseProperty<ActionsConfig>
{
    public ActionsConfigProperty(Form form, String key, ActionsConfig value)
    {
        super(form, key, value);
    }

    @Override
    public void set(ActionsConfig value)
    {
        boolean same = Objects.equals(this.value, value);

        super.set(value);

        if (!same)
        {
            this.updateAnimator();
        }
    }

    @Override
    public void tween(ActionsConfig newValue, int duration, IInterpolation interpolation, int offset, boolean playing)
    {
        boolean same = Objects.equals(this.value, value);

        super.tween(newValue, duration, interpolation, offset, playing);

        if (!same)
        {
            this.updateAnimator();
        }
    }

    private void updateAnimator()
    {
        if (this.form instanceof ModelForm)
        {
            ModelForm form = (ModelForm) this.form;
            Animator animator = form.getAnimator();

            if (animator != null)
            {
                animator.setup(form.getModel(), value);
            }
        }
    }

    @Override
    protected void propertyFromData(MapType data, String key)
    {
        this.value.fromData(data.getMap(key));
    }

    @Override
    public void toData(MapType data)
    {
        data.put(this.getKey(), this.value.toData());
    }
}