package mchorse.bbs.ui.framework.elements;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI elements collection
 * 
 * This class is responsible for handling a collection of elements
 */
public class UIElements <T extends IUIElement> implements IUIElement
{
    /**
     * List of elements 
     */
    public List<T> elements = new ArrayList<T>();

    /**
     * Whether this element is enabled (can handle any input) 
     */
    protected boolean enabled = true;

    /**
     * Whether this element is visible 
     */
    protected boolean visible = true;

    /**
     * Parent of this elements collection
     */
    private UIElement parent;

    public UIElements(UIElement parent)
    {
        this.parent = parent;
    }

    public void clear()
    {
        this.elements.clear();
    }

    public void prepend(T element)
    {
        if (element != null)
        {
            this.elements.add(0, element);
        }
    }

    public void add(T element)
    {
        if (element != null)
        {
            this.elements.add(element);
        }
    }

    public boolean addAfter(T target, T element)
    {
        int index = this.elements.indexOf(target);

        if (index != -1 && element != null)
        {
            if (index + 1 >= this.elements.size())
            {
                this.elements.add(element);
            }
            else
            {
                this.elements.add(index + 1, element);
            }

            return true;
        }

        return false;
    }

    public boolean addBefore(T target, T element)
    {
        int index = this.elements.indexOf(target);

        if (index != -1 && element != null)
        {
            this.elements.add(index, element);

            return true;
        }

        return false;
    }

    public void add(T... elements)
    {
        for (T element : elements)
        {
            if (element != null) this.elements.add(element);
        }
    }

    @Override
    public void resize()
    {
        for (T element : this.elements)
        {
            element.resize();
        }
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled && this.visible;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    @Override
    public boolean mouseClicked(UIContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.mouseClicked(context))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(UIContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.mouseScrolled(context))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(UIContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.mouseReleased(context))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(UIContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.keyPressed(context))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean textInput(UIContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.textInput(context))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canBeRendered(Area viewport)
    {
        return true;
    }

    @Override
    public void render(UIContext context)
    {
        for (T element : this.elements)
        {
            if (element.isVisible() && element.canBeRendered(context.getViewport()))
            {
                element.render(context);
            }
        }
    }
}