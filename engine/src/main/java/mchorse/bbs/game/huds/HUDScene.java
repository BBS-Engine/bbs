package mchorse.bbs.game.huds;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.manager.data.AbstractData;

import java.util.ArrayList;
import java.util.List;

public class HUDScene extends AbstractData
{
    public List<HUDForm> forms = new ArrayList<HUDForm>();
    public float fov = 70F;

    public boolean update(boolean allowExpiring)
    {
        this.forms.removeIf((form) -> form.update(allowExpiring));

        return allowExpiring && this.forms.isEmpty();
    }

    @Override
    public void toData(MapType data)
    {
        ListType forms = new ListType();

        for (HUDForm form : this.forms)
        {
            forms.add(form.toData());
        }

        data.put("forms", forms);
        data.putFloat("fov", this.fov);
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("forms"))
        {
            ListType list = data.getList("forms");

            for (int i = 0; i < list.size(); i++)
            {
                HUDForm form = new HUDForm();

                form.fromData(list.getMap(i));
                this.forms.add(form);
            }
        }

        if (data.has("fov"))
        {
            this.fov = data.getFloat("fov");
        }
    }
}