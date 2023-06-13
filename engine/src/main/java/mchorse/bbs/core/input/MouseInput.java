package mchorse.bbs.core.input;

import mchorse.bbs.graphics.window.Window;
import org.lwjgl.glfw.GLFW;

/**
 * Mouse input class
 * 
 * This class is responsible for handling mouse input.
 */
public class MouseInput
{
    private IMouseHandler handler;
    private boolean inWindow = true;

    public int x;
    public int y;

    public int lastScrollX;
    public int lastScrollY;

    private long lastScrollTime;

    public MouseInput(IMouseHandler handler)
    {
        this.handler = handler;
    }

    public void init()
    {
        long win = Window.getWindow();
        double[] x = new double[1];
        double[] y = new double[1];

        GLFW.glfwGetCursorPos(win, x, y);

        this.x = (int) x[0];
        this.y = (int) y[0];

        GLFW.glfwSetCursorPosCallback(win, (windowHandle, xpos, ypos) ->
        {
            this.x = (int) xpos;
            this.y = (int) ypos;
        });

        GLFW.glfwSetCursorEnterCallback(win, (windowHandle, entered) ->
        {
            this.inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(win, (windowHandle, button, action, mode) ->
        {
            if (this.handler != null)
            {
                this.handler.handleMouse(button, action, mode);
            }
        });

        GLFW.glfwSetScrollCallback(win, (windowHandle, scrollX, scrollY) ->
        {
            this.lastScrollX = (int) scrollX;
            this.lastScrollY = (int) scrollY;
            this.lastScrollTime = System.currentTimeMillis();

            if (this.handler != null)
            {
                this.handler.handleScroll(scrollX, scrollY);
            }
        });
    }

    /**
     * Check whether the cursor inside the window
     */
    public boolean isInWindow()
    {
        return this.inWindow;
    }

    public void update()
    {
        if (System.currentTimeMillis() - this.lastScrollTime > 50)
        {
            this.lastScrollX = 0;
            this.lastScrollY = 0;
        }
    }
}