package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;

import java.util.ArrayList;
import java.util.List;

public abstract class UIParentComponent extends UIComponent
{
    public List<UIComponent> children = new ArrayList<UIComponent>();

    @Override
    @DiscardMethod
    public List<UIComponent> getChildComponents()
    {
        return this.children;
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        ListType list = new ListType();

        for (UIComponent component : this.children)
        {
            list.add(BBS.getFactoryUIComponents().toData(component));
        }

        data.put("components", list);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        ListType list = data.getList("components");

        for (int i = 0, c = list.size(); i < c; i++)
        {
            UIComponent component = BBS.getFactoryUIComponents().fromData(list.getMap(i));

            if (component != null)
            {
                this.children.add(component);
            }
        }
    }
}