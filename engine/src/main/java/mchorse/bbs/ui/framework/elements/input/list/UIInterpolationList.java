package mchorse.bbs.ui.framework.elements.input.list;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.math.Interpolation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interpolations list
 */
public class UIInterpolationList extends UIList<Interpolation>
{
    public UIInterpolationList(Consumer<List<Interpolation>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;

        for (Interpolation interp : Interpolation.values())
        {
            this.add(interp);
        }

        this.background().cancelScrollEdge().sort();
    }

    @Override
    protected boolean sortElements()
    {
        Collections.sort(this.list, Comparator.comparing(o -> o.key));

        return true;
    }

    @Override
    protected String elementToString(UIContext context, int i, Interpolation element)
    {
        return element.getName().get();
    }
}