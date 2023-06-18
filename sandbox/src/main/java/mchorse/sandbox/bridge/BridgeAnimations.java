package mchorse.sandbox.bridge;

import mchorse.sandbox.SandboxEngine;
import mchorse.bbs.animation.Animations;
import mchorse.bbs.bridge.IBridgeAnimations;

public class BridgeAnimations extends BaseBridge implements IBridgeAnimations
{
    public BridgeAnimations(SandboxEngine engine)
    {
        super(engine);
    }

    @Override
    public Animations getAnimations()
    {
        return this.engine.renderer.animations;
    }
}