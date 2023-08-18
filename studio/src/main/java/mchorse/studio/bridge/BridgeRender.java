package mchorse.studio.bridge;

import mchorse.bbs.bridge.IBridgeRender;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.studio.StudioEngine;

public class BridgeRender extends BaseBridge implements IBridgeRender
{
    public BridgeRender(StudioEngine engine)
    {
        super(engine);
    }

    @Override
    public void renderSceneTo(Camera camera, Framebuffer framebuffer, int pass, boolean renderScreen, float quality, Runnable rendering)
    {
        this.engine.renderer.renderFrameToQuality(camera, framebuffer, pass, renderScreen, quality, rendering);
    }
}