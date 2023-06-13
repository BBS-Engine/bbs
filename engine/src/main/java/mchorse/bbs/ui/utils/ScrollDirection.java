package mchorse.bbs.ui.utils;

/**
 * Scroll direction
 */
public enum ScrollDirection
{
    VERTICAL()
    {
        @Override
        public int getPosition(Area area, float x)
        {
            return area.y(x);
        }

        @Override
        public int getSide(Area area)
        {
            return area.h;
        }

        @Override
        public int getScroll(ScrollArea area, int x, int y)
        {
            return y - area.y + area.scroll;
        }

        @Override
        public float getProgress(Area area, int x, int y)
        {
            return (y - area.y) / (float) area.h;
        }
    },
    HORIZONTAL()
    {
        @Override
        public int getPosition(Area area, float x)
        {
            return area.x(x);
        }

        @Override
        public int getSide(Area area)
        {
            return area.w;
        }

        @Override
        public int getScroll(ScrollArea area, int x, int y)
        {
            return x - area.x + area.scroll;
        }

        @Override
        public float getProgress(Area area, int x, int y)
        {
            return (x - area.x) / (float) area.w;
        }
    };

    /**
     * Get position of the area, x = 0 minimum corner, x = 1 maximum corner
     */
    public abstract int getPosition(Area area,  float x);

    /**
     * Get dominant side for this scrolling direction
     */
    public abstract int getSide(Area area);

    /**
     * Get scrolled amount for given mouse position
     */
    public abstract int getScroll(ScrollArea area, int x, int y);

    /**
     * Get progress scalar between 0 and 1 which identifies how much
     * it is near the maximum side
     */
    public abstract float getProgress(Area area, int x, int y);
}