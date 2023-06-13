package mchorse.bbs.ui.camera.utils;

import mchorse.bbs.camera.data.InterpolationType;
import mchorse.bbs.ui.framework.elements.input.list.UIList;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class UIInterpolationTypeList extends UIList<InterpolationType>
{
    public UIInterpolationTypeList(Consumer<List<InterpolationType>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;

        for (InterpolationType interp : InterpolationType.values())
        {
            this.add(interp);
        }

        this.background().cancelScrollEdge().sort();
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort(Comparator.comparing(o -> o.name));

        return true;
    }

    @Override
    protected String elementToString(int i, InterpolationType element)
    {
        return element.getName().get();
    }
}