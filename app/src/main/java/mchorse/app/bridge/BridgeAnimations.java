package mchorse.app.bridge;

import mchorse.app.GameEngine;
import mchorse.bbs.animation.Animations;
import mchorse.bbs.bridge.IBridgeAnimations;

public class BridgeAnimations extends BaseBridge implements IBridgeAnimations
{
    public BridgeAnimations(GameEngine engine)
    {
        super(engine);
    }

    @Override
    public Animations getAnimations()
    {
        return this.engine.renderer.animations;
    }
}