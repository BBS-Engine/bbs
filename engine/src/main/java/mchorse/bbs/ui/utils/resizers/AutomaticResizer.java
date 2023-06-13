package mchorse.bbs.ui.utils.resizers;

import mchorse.bbs.ui.framework.elements.IUIElement;
import mchorse.bbs.ui.framework.elements.UIElement;

import java.util.ArrayList;
import java.util.List;

public abstract class AutomaticResizer extends BaseResizer
{
    public UIElement parent;
    public int margin;
    public int padding;
    public int height;

    public AutomaticResizer(UIElement parent, int margin)
    {
        this.parent = parent;
        this.margin = margin;

        this.setup();
    }

    /* Standard properties */

    public AutomaticResizer padding(int padding)
    {
        this.padding = padding;

        return this;
    }

    public AutomaticResizer height(int height)
    {
        this.height = height;

        return this;
    }

    /* Child management */

    public void setup()
    {
        for (IUIElement child : this.parent.getChildren())
        {
            if (child instanceof UIElement)
            {
                UIElement element = (UIElement) child;

                element.resizer(this.child(element));
            }
        }
    }

    public IResizer child(UIElement element)
    {
        ChildResizer child = new ChildResizer(this, element);

        return child;
    }

    public List<ChildResizer> getResizers()
    {
        List<ChildResizer> resizers = new ArrayList<ChildResizer>();

        for (IUIElement element : this.parent.getChildren())
        {
            if (element instanceof UIElement)
            {
                UIElement elem = (UIElement) element;

                if (elem.resizer() instanceof ChildResizer)
                {
                    resizers.add((ChildResizer) elem.resizer());
                }
            }
        }

        return resizers;
    }

    /* Miscellaneous */

    @Override
    public void add(UIElement parent, UIElement child)
    {
        child.resizer(this.child(child));
    }

    @Override
    public void remove(UIElement parent, UIElement child)
    {
        IResizer resizer = child.resizer();

        if (resizer instanceof ChildResizer)
        {
            child.resizer(((ChildResizer) resizer).resizer);
        }
    }

    @Override
    public int getX()
    {
        return 0;
    }

    @Override
    public int getY()
    {
        return 0;
    }

    @Override
    public int getW()
    {
        return 0;
    }

    @Override
    public int getH()
    {
        return 0;
    }
}