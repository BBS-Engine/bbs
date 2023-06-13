package mchorse.bbs.game.conditions.blocks;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

public class FormConditionBlock extends TargetConditionBlock
{
    public MapType form;
    public boolean onlyName;

    private Form compiled;

    @Override
    protected TargetMode getDefaultTarget()
    {
        return TargetMode.PLAYER;
    }

    @Override
    protected boolean evaluateBlock(DataContext context)
    {
        Entity entity = this.target.getEntity(context);
        Form form = this.getForm(entity);

        if (form == null && this.form == null)
        {
            return true;
        }

        if (form == null || this.form == null)
        {
            return false;
        }

        if (this.onlyName)
        {
            return form.getId().equals(this.form.getString("id"));
        }

        if (this.compiled == null)
        {
            this.compiled = FormUtils.fromData(this.form);
        }

        return form.equals(this.compiled);
    }

    private Form getForm(Entity entity)
    {
        Form form = null;
        FormComponent component = entity.get(FormComponent.class);

        if (component != null)
        {
            form = component.form;
        }

        return form;
    }

    @Override
    public String stringify()
    {
        if (this.form == null)
        {
            return UIKeys.CONDITIONS_FORM_NO_FORM.get();
        }

        return UIKeys.CONDITIONS_FORM_STRING.formatString(this.form.getString("id"));
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (this.form != null)
        {
            data.put("form", this.form);
        }

        data.putBool("onlyName", this.onlyName);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.form = null;
        this.compiled = null;

        if (data.has("form"))
        {
            this.form = data.getMap("form");
        }

        this.onlyName = data.getBool("onlyName");
    }
}