package mchorse.bbs.film.values;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.recording.values.ValueForm;
import mchorse.bbs.recording.values.ValueFrames;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.utils.clips.values.ValueClips;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

import java.util.Objects;

public class ValueReplay extends ValueGroup
{
    public final ValueForm form = new ValueForm("form");
    public final ValueFrames keyframes = new ValueFrames("keyframes");
    public final ValueClips clips = new ValueClips("clips", BBS.getFactoryActions());

    public ValueReplay(String id)
    {
        super(id);

        this.add(this.form);
        this.add(this.keyframes);
        this.add(this.clips);
    }

    public void apply(Entity actor)
    {
        FormComponent component = actor.get(FormComponent.class);

        if (component != null && !Objects.equals(component.form, this.form.get()))
        {
            component.setForm(FormUtils.copy(this.form.get()));
        }
    }
}