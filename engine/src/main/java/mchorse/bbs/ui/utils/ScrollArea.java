package mchorse.bbs.ui.utils;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.UIDraw;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;

/**
 * Scrollable area
 * 
 * This class is responsible for storing information for scrollable one 
 * directional objects. 
 */
public class ScrollArea extends Area
{
    /**
     * Size of an element/item in the scroll area
     */
    public int scrollItemSize;

    /**
     * Size of the scrolling area 
     */
    public int scrollSize;

    /**
     * Scroll position 
     */
    public int scroll;

    /**
     * Whether this scroll area gets dragged 
     */
    public boolean dragging;

    /**
     * Speed of how fast shit's scrolling  
     */
    public int scrollSpeed = 10;

    /**
     * Scroll direction, used primarily in the {@link #clamp()} method 
     */
    public ScrollDirection direction = ScrollDirection.VERTICAL;

    /**
     * Whether the scrollbar should be on opposite side (default is right
     * for vertical and bottom for horizontal)
     */
    public boolean opposite;

    /**
     * Width of scroll bar
     */
    public int scrollbarWidth = -1;

    /**
     * Whether this scroll area should cancel mouse events when mouse scroll
     * reaches the end
     */
    public boolean cancelScrollEdge = false;

    /**
     * Whether scrollbars should be rendered
     */
    public boolean drawScrollbars = true;

    public static void bar(UIDraw draw, int x1, int y1, int x2, int y2, int color)
    {
        draw.dropShadow(x1, y1, x2, y2, 5, color, Colors.setA(color, 0F));

        draw.box(x1, y1, x2, y2, 0xffeeeeee);
        draw.box(x1 + 1, y1 + 1, x2, y2, 0xff666666);
        draw.box(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xffaaaaaa);

        int dx = x2 - x1;
        int dy = y2 - y1;

        if (dx + dy < 30)
        {
            return;
        }

        int x = (x2 + x1) / 2;
        int y = (y2 + y1) / 2;

        /* Little handle */
        if (dx > dy)
        {
            draw.box(x - 3, y - 1, x - 2, y + 1, Colors.GRAY);
            draw.box(x, y - 1, x + 1, y + 1, Colors.GRAY);
            draw.box(x + 3, y - 1, x + 4, y + 1, Colors.GRAY);
        }
        else
        {
            draw.box(x - 1, y - 3, x + 1, y - 2, Colors.GRAY);
            draw.box(x - 1, y, x + 1, y + 1, Colors.GRAY);
            draw.box(x - 1, y + 3, x + 1, y + 4, Colors.GRAY);
        }
    }

    public ScrollArea(int x, int y, int w, int h)
    {
        super(x, y, w, h);
    }

    public ScrollArea()
    {}

    public ScrollArea(int itemSize)
    {
        this.scrollItemSize = itemSize;
    }

    public ScrollArea(int itemSize, ScrollDirection direction)
    {
        this(itemSize);

        this.direction = direction;
    }

    public ScrollArea cancelScrolling()
    {
        this.cancelScrollEdge = true;

        return this;
    }

    public ScrollArea opposite()
    {
        this.opposite = true;

        return this;
    }

    public int getScrollbarWidth()
    {
        return this.scrollbarWidth <= 0 ? BBSSettings.scrollbarWidth.get() : this.scrollbarWidth;
    }

    public void setSize(int items)
    {
        this.scrollSize = items * this.scrollItemSize;
    }

    /**
     * Scroll by relative amount 
     */
    public void scrollBy(int x)
    {
        this.scroll += x;
        this.clamp();
    }

    /**
     * Scroll to the position in the scroll area 
     */
    public void scrollTo(int x)
    {
        this.scroll = x;
        this.clamp();
    }

    public void scrollToEnd()
    {
        this.scrollTo(Integer.MAX_VALUE);
    }

    public void scrollIntoView(int x)
    {
        this.scrollIntoView(x, this.scrollItemSize, 0);
    }

    public void scrollIntoView(int x, int bottomOffset)
    {
        this.scrollIntoView(x, bottomOffset, 0);
    }

    public void scrollIntoView(int x, int bottomOffset, int topOffset)
    {
        if (this.scroll + topOffset > x)
        {
            this.scrollTo(x - topOffset);
        }
        else if (x > this.scroll + this.direction.getSide(this) - bottomOffset)
        {
            this.scrollTo(x - this.direction.getSide(this) + bottomOffset);
        }
    }

    /**
     * Clamp scroll to the bounds of the scroll size; 
     */
    public void clamp()
    {
        int size = this.direction.getSide(this);

        if (this.scrollSize <= size)
        {
            this.scroll = 0;
        }
        else
        {
            this.scroll = MathUtils.clamp(this.scroll, 0, this.scrollSize - size);
        }
    }

    /**
     * Get index of the cursor based on the {@link #scrollItemSize}.  
     */
    public int getIndex(int x, int y)
    {
        int axis = this.direction.getScroll(this, x, y);
        int index = axis / this.scrollItemSize;

        if (axis < 0)
        {
            return -1;
        }
        else if (axis > this.scrollSize)
        {
            return -2;
        }

        return index > this.scrollSize / this.scrollItemSize ? -1 : index;
    }

    /**
     * Calculates scroll bar's height 
     */
    public int getScrollBar(int size)
    {
        int maxSize = this.direction.getSide(this);

        if (this.scrollSize < size)
        {
            return 0;
        }

        return (int) ((1.0F - ((this.scrollSize - maxSize) / (float) this.scrollSize)) * size);
    }

    /* GUI code for easier manipulations */

    public boolean mouseClicked(UIContext context)
    {
        return context.mouseButton == 0 && this.mouseClicked(context.mouseX, context.mouseY);
    }

    /**
     * This method should be invoked to register dragging 
     */
    public boolean mouseClicked(int x, int y)
    {
        boolean isInside = this.isInside(x, y) && this.scrollSize > this.h;

        if (!this.drawScrollbars)
        {
            return false;
        }

        if (isInside)
        {
            int scrollbar = this.getScrollbarWidth();

            if (this.opposite)
            {
                isInside = this.direction == ScrollDirection.VERTICAL ? x <= this.x + scrollbar : y <= this.y + scrollbar;
            }
            else
            {
                isInside = this.direction == ScrollDirection.VERTICAL ? x >= this.ex() - scrollbar : y >= this.ey() - scrollbar;
            }
        }

        if (isInside)
        {
            this.dragging = true;
        }

        return isInside;
    }

    public boolean mouseScroll(UIContext context)
    {
        return this.mouseScroll(context.mouseX, context.mouseY, context.mouseWheel);
    }

    /**
     * This method should be invoked when mouse wheel is scrolling 
     */
    public boolean mouseScroll(int x, int y, int scroll)
    {
        scroll = -scroll;

        boolean isInside = this.isInside(x, y);
        int lastScroll = this.scroll;

        if (isInside)
        {
            this.scrollBy((int) Math.copySign(this.scrollSpeed, scroll));
        }

        return isInside && (this.cancelScrollEdge || lastScroll != this.scroll);
    }

    public void mouseReleased(UIContext context)
    {
        this.mouseReleased(context.mouseX, context.mouseY);
    }

    /**
     * When mouse button gets released
     */
    public void mouseReleased(int x, int y)
    {
        this.dragging = false;
    }

    public void drag(UIContext context)
    {
        this.drag(context.mouseX, context.mouseY);
    }

    /**
     * This should be invoked in a rendering or and update method. It's
     * responsible for scrolling through this view when dragging. 
     */
    public void drag(int x, int y)
    {
        if (this.dragging)
        {
            float progress = this.direction.getProgress(this, x, y);

            this.scrollTo((int) (progress * (this.scrollSize - this.direction.getSide(this) + this.getScrollbarWidth())));
        }
    }

    /**
     * This method is responsible for render a scroll bar
     */
    public void renderScrollbar(UIDraw draw)
    {
        int side = this.direction.getSide(this);

        if (this.scrollSize <= side)
        {
            return;
        }

        int shadow = Colors.mulRGB(Colors.A50 | BBSSettings.primaryColor.get(), 0.75F);

        if (this.drawScrollbars)
        {
            int scrollbar = this.getScrollbarWidth();
            int h = Math.max(this.getScrollBar(side / 2), 4);
            int x = this.opposite ? this.x : this.ex() - scrollbar;
            /* Sometimes I don't understand how I come up with such clever
             * formulas, but it's all ratios, y'all */
            int y = this.y + (int) ((this.scroll / (float) (this.scrollSize - this.h)) * (this.h - h));
            int rx = x + scrollbar;
            int ry = y + h;

            if (this.direction == ScrollDirection.HORIZONTAL)
            {
                y = this.opposite ? this.y : this.ey() - scrollbar;
                x = this.x + (int) ((this.scroll / (float) (this.scrollSize - this.w)) * (this.w - h));
                rx = x + h;
                ry = y + scrollbar;
            }

            int color = BBSSettings.scrollbarShadow.get();

            bar(draw, x, y, rx, ry, color);
        }
        else if (this.direction == ScrollDirection.VERTICAL)
        {
            if (this.scroll > 0)
            {
                draw.gradientVBox(this.x, this.y, this.ex(), this.y + 20, shadow, 0);
            }

            if (this.scroll < this.scrollSize - side)
            {
                draw.gradientVBox(this.x, this.ey() - 20, this.ex(), this.ey(), 0, shadow);
            }
        }
        else if (this.direction == ScrollDirection.HORIZONTAL)
        {
            if (this.scroll > 0)
            {
                draw.gradientHBox(this.x, this.y, this.x + 20, this.ey(), shadow, 0);
            }

            if (this.scroll < this.scrollSize - side)
            {
                draw.gradientHBox(this.ex() - 20, this.y, this.ex(), this.ey(), 0, shadow);
            }
        }
    }
}