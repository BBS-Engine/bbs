package mchorse.studio.bridge;

import mchorse.studio.StudioEngine;
import mchorse.bbs.animation.Animations;
import mchorse.bbs.bridge.IBridgeAnimations;

public class BridgeAnimations extends BaseBridge implements IBridgeAnimations
{
    public BridgeAnimations(StudioEngine engine)
    {
        super(engine);
    }

    @Override
    public Animations getAnimations()
    {
        return this.engine.renderer.animations;
    }
}