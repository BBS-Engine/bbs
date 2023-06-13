package mchorse.bbs.forms;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.Form;

public class FormUtils
{
    public static Form fromData(MapType data)
    {
        return data == null ? null : BBS.getForms().fromData(data);
    }

    public static MapType toData(Form form)
    {
        return form == null ? null : BBS.getForms().toData(form);
    }

    public static Form copy(Form form)
    {
        return form == null ? null : form.copy();
    }
}