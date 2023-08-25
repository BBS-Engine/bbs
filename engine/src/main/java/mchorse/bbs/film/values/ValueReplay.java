package mchorse.bbs.film.values;

import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

import java.util.List;
import java.util.Objects;

public class ValueReplay extends ValueGroup
{
    public final ValueForm form = new ValueForm("form");
    public final ValueFrames keyframes = new ValueFrames("keyframes");

    public ValueReplay(String id)
    {
        super(id);

        this.add(this.form);
        this.add(this.keyframes);
    }

    void remapId(String id)
    {
        this.id = id;
    }

    public void apply(Entity actor)
    {
        FormComponent component = actor.get(FormComponent.class);

        if (component != null && !Objects.equals(component.form, this.form.get()))
        {
            component.setForm(FormUtils.copy(this.form.get()));
        }
    }

    public void applyFrame(int tick, Entity actor)
    {
        this.applyFrame(tick, actor, null);
    }

    public void applyFrame(int tick, Entity actor, List<String> groups)
    {
        this.keyframes.apply(tick, actor, groups);
    }
}