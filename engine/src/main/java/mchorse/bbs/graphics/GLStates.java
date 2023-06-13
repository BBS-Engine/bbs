package mchorse.bbs.graphics;

import mchorse.bbs.graphics.window.Window;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class GLStates
{
    public static void alpha(boolean on)
    {
        toggleState(GL11.GL_ALPHA, on);
    }

    public static void blending(boolean on)
    {
        toggleState(GL11.GL_BLEND, on);
    }

    public static void cullFaces(boolean on)
    {
        toggleState(GL11.GL_CULL_FACE, on);
    }

    public static void depthMask(boolean on)
    {
        GL11.glDepthMask(on);
    }

    public static void depthTest(boolean on)
    {
        toggleState(GL11.GL_DEPTH_TEST, on);
    }

    public static void scissorTest(boolean on)
    {
        toggleState(GL11.GL_SCISSOR_TEST, on);
    }

    private static void toggleState(int state, boolean on)
    {
        if (on)
        {
            GL11.glEnable(state);
        }
        else
        {
            GL11.glDisable(state);
        }
    }

    public static void setupBlendingFunction()
    {
        GL15.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA, GL11.GL_ONE);
    }

    public static void setupDepthFunction3D()
    {
        GL11.glDepthFunc(GL11.GL_LESS);
    }

    public static void setupDepthFunction2D()
    {
        GL11.glDepthFunc(GL11.GL_ALWAYS);
    }

    public static void resetViewport()
    {
        int pixelDensity = Window.getPixelDensity();

        GL11.glViewport(0, 0, Window.width * pixelDensity, Window.height * pixelDensity);
    }

    public static void scissor(int x, int y, int w, int h)
    {
        int pixelDensity = Window.getPixelDensity();

        GL11.glScissor(x * pixelDensity, y * pixelDensity, w * pixelDensity, h * pixelDensity);
    }
}