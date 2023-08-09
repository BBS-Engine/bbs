package mchorse.bbs.recording.values;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.settings.values.base.BaseValueBasic;

public class ValueForm extends BaseValueBasic<Form>
{
    public ValueForm(String id)
    {
        super(id);
    }

    @Override
    public BaseType toData()
    {
        return this.value == null ? null : FormUtils.toData(this.value);
    }

    @Override
    public void fromData(BaseType data)
    {
        if (data.isMap())
        {
            this.set(FormUtils.fromData(data.asMap()));
        }
    }
}