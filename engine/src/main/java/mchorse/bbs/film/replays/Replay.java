package mchorse.bbs.film.replays;

import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.settings.values.ValueForm;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

import java.util.List;

public class Replay extends ValueGroup
{
    public final ValueForm form = new ValueForm("form");
    public final ReplayKeyframes keyframes = new ReplayKeyframes("keyframes");
    public final FormProperties properties = new FormProperties("properties");

    public Replay(String id)
    {
        super(id);

        this.add(this.form);
        this.add(this.keyframes);
        this.add(this.properties);
    }

    public void applyFrame(int tick, Entity actor)
    {
        this.applyFrame(tick, actor, null);
    }

    public void applyFrame(int tick, Entity actor, List<String> groups)
    {
        this.keyframes.apply(tick, actor, groups);
    }

    public void applyProperties(int tick, Entity entity, boolean playing)
    {
        Form form = entity.get(FormComponent.class).form;

        if (form == null)
        {
            return;
        }

        for (BaseValue value : this.properties.getAll())
        {
            if (!(value instanceof FormProperty))
            {
                continue;
            }

            FormProperty formProperty = (FormProperty) value;
            IFormProperty property = FormUtils.getProperty(form, formProperty.getId());
            Pair segment = formProperty.get().findSegment(tick);

            if (segment != null)
            {
                GenericKeyframe a = (GenericKeyframe) segment.a;
                GenericKeyframe b = (GenericKeyframe) segment.b;
                int duration = (int) (b.tick - a.tick);
                int offset = (int) (tick - a.tick);

                if (a == b)
                {
                    property.set(a.value);
                }
                else
                {
                    property.tween(b.value, a.value, duration, a.interp, offset, playing);
                }
            }
            else
            {
                Form replayForm = this.form.get();

                if (replayForm != null)
                {
                    IFormProperty replayProperty = FormUtils.getProperty(replayForm, formProperty.getId());

                    if (replayProperty != null)
                    {
                        property.set(replayProperty.get());
                    }
                }
            }
        }
    }
}