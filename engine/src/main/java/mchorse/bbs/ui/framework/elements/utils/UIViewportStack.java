package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.IViewport;
import mchorse.bbs.ui.utils.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Viewport stack
 *
 * This class is responsible for calculating and keeping track of
 * embedded (into each other) scrolling areas
 */
public class UIViewportStack implements IViewportStack
{
    private Stack<Area> viewportStack = new Stack<>();
    private List<Area> viewportAreas = new ArrayList<>();
    private int shiftX;
    private int shiftY;

    public static UIViewportStack fromElement(UIElement element)
    {
        UIViewportStack stack = new UIViewportStack();

        stack.applyFromElement(element);

        return stack;
    }

    public void applyFromElement(UIElement element)
    {
        List<IViewport> elements = new ArrayList<>();

        while (element != null)
        {
            if (element instanceof IViewport)
            {
                elements.add((IViewport) element);
            }

            element = element.getParent();
        }

        for (int i = elements.size() - 1; i >= 0; i--)
        {
            elements.get(i).apply(this);
        }
    }

    @Override
    public void reset()
    {
        this.shiftX = 0;
        this.shiftY = 0;

        this.viewportStack.clear();
    }

    @Override
    public Area getViewport()
    {
        return this.viewportStack.isEmpty() ? null : this.viewportStack.peek();
    }

    @Override
    public void pushViewport(Area area)
    {
        if (this.viewportStack.isEmpty())
        {
            Area child = this.getCurrentViewportArea();

            child.copy(area);
            this.viewportStack.push(child);
        }
        else
        {
            Area current = this.viewportStack.peek();
            Area child = this.getCurrentViewportArea();

            child.copy(area);
            current.clamp(child);
            this.viewportStack.push(child);
        }
    }

    private Area getCurrentViewportArea()
    {
        while (this.viewportAreas.size() < this.viewportStack.size() + 1)
        {
            this.viewportAreas.add(new Area());
        }

        return this.viewportAreas.get(this.viewportStack.size());
    }

    @Override
    public void popViewport()
    {
        this.viewportStack.pop();
    }

    @Override
    public int getShiftX()
    {
        return this.shiftX;
    }

    @Override
    public int getShiftY()
    {
        return this.shiftY;
    }

    /**
     * Get global X (relative to root element/screen)
     */
    @Override
    public int globalX(int x)
    {
        return x - this.shiftX;
    }

    /**
     * Get global Y (relative to root element/screen)
     */
    @Override
    public int globalY(int y)
    {
        return y - this.shiftY;
    }

    /**
     * Get current local X (relative to current viewport)
     */
    @Override
    public int localX(int x)
    {
        return x + this.shiftX;
    }

    /**
     * Get current local Y (relative to current viewport)
     */
    @Override
    public int localY(int y)
    {
        return y + this.shiftY;
    }

    @Override
    public void shiftX(int x)
    {
        this.shiftX += x;

        if (!this.viewportStack.isEmpty())
        {
            this.viewportStack.peek().x += x;
        }
    }

    @Override
    public void shiftY(int y)
    {
        this.shiftY += y;

        if (!this.viewportStack.isEmpty())
        {
            this.viewportStack.peek().y += y;
        }
    }
}