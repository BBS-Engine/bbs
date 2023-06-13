package mchorse.app.bridge;

import mchorse.app.GameEngine;
import mchorse.bbs.bridge.IBridgeRender;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.Framebuffer;

public class BridgeRender extends BaseBridge implements IBridgeRender
{
    public BridgeRender(GameEngine engine)
    {
        super(engine);
    }

    @Override
    public Framebuffer getMainFramebuffer()
    {
        return this.engine.renderer.finalFramebuffer;
    }

    @Override
    public void renderSceneTo(Camera camera, Framebuffer framebuffer, int pass, boolean renderScreen, float quality)
    {
        this.engine.renderer.renderFrameToQuality(camera, framebuffer, pass, renderScreen, quality);
    }
}