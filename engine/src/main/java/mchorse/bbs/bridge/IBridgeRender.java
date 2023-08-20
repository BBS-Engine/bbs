package mchorse.bbs.bridge;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.Framebuffer;

import java.util.function.Consumer;

public interface IBridgeRender
{
    public default void renderSceneTo(Camera camera, Framebuffer framebuffer, int pass)
    {
        this.renderSceneTo(camera, framebuffer, pass, 0);
    }

    public default void renderSceneTo(Camera camera, Framebuffer framebuffer, int pass, float quality)
    {
        this.renderSceneTo(camera, framebuffer, pass, false, quality);
    }

    public default void renderSceneTo(Camera camera, Framebuffer framebuffer, int pass, boolean renderScreen, float quality)
    {
        this.renderSceneTo(camera, framebuffer, pass, renderScreen, quality, null);
    }

    public void renderSceneTo(Camera camera, Framebuffer framebuffer, int pass, boolean renderScreen, float quality, Consumer<Framebuffer> rendering);
}