package mchorse.bbs.film.replays;

import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.settings.values.ValueForm;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeSegment;
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
            if (value instanceof GenericKeyframeChannel)
            {
                this.applyProperty(tick, playing, form, (GenericKeyframeChannel) value);
            }
        }
    }

    private void applyProperty(int tick, boolean playing, Form form, GenericKeyframeChannel value)
    {
        IFormProperty property = FormUtils.getProperty(form, value.getId());

        if (property == null)
        {
            return;
        }

        GenericKeyframeSegment segment = value.find(tick);

        if (segment != null)
        {
            if (segment.isSame() || segment.a.isInstant())
            {
                property.set(segment.a.getValue());
            }
            else
            {
                property.tween(segment.b.getValue(), segment.a.getValue(), segment.duration, segment.a.getInterpolation(), (int) segment.offset, playing);
            }
        }
        else
        {
            Form replayForm = this.form.get();

            if (replayForm != null)
            {
                IFormProperty replayProperty = FormUtils.getProperty(replayForm, value.getId());

                if (replayProperty != null)
                {
                    property.set(replayProperty.get());
                }
            }
        }
    }
}