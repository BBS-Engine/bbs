package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interpolations list
 */
public class UIKeyframeInterpolationsList extends UIList<KeyframeInterpolation>
{
    public UIKeyframeInterpolationsList(Consumer<List<KeyframeInterpolation>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;

        for (KeyframeInterpolation interp : KeyframeInterpolation.values())
        {
            this.add(interp);
        }

        this.sort();
        this.background();
    }

    @Override
    protected boolean sortElements()
    {
        this.list.sort(Comparator.comparing(o -> o.key));

        return true;
    }

    @Override
    protected String elementToString(int i, KeyframeInterpolation element)
    {
        return element.getKey().get();
    }
}