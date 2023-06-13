package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.ui.utils.Area;

/**
 * General interface for viewport stack
 */
public interface IViewportStack
{
    public void reset();

    public Area getViewport();

    public void pushViewport(Area area);

    public void popViewport();

    public int getShiftX();

    public int getShiftY();

    /**
     * Get global X (relative to root element/screen)
     */
    public int globalX(int x);

    /**
     * Get global Y (relative to root element/screen)
     */
    public int globalY(int y);

    /**
     * Get current local X (relative to current viewport)
     */
    public int localX(int x);

    /**
     * Get current local Y (relative to current viewport)
     */
    public int localY(int y);

    public void shiftX(int x);

    public void shiftY(int y);
}