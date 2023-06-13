package mchorse.bbs.core.input;

/**
 * Mouse handler interface 
 */
public interface IMouseHandler
{
    public void handleMouse(int button, int action, int mode);

    public void handleScroll(double x, double y);
}