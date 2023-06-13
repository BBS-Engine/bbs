package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.Target;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

public class FormTriggerBlock extends TriggerBlock
{
    public Target target = new Target(TargetMode.SUBJECT);
    public MapType form;

    @Override
    public String stringify()
    {
        if (this.form == null)
        {
            return UIKeys.TRIGGERS_FORM_DETRANSFORM.get();
        }

        return UIKeys.TRIGGERS_FORM_FORM.formatString(this.form.getString("id"));
    }

    @Override
    public void trigger(DataContext context)
    {
        Entity entity = this.target.getEntity(context);
        FormComponent form = entity.get(FormComponent.class);

        if (form != null)
        {
            form.setForm(FormUtils.fromData(this.form));
        }
    }

    @Override
    public boolean isEmpty()
    {
        return this.form == null;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("target", this.target.toData());

        if (this.form != null)
        {
            data.put("form", this.form);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("target"))
        {
            this.target.fromData(data.getMap("target"));
        }

        this.form = null;

        if (data.has("form"))
        {
            this.form = data.getMap("form");
        }
    }
}