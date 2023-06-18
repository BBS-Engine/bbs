package mchorse.sandbox.bridge;

import mchorse.sandbox.SandboxEngine;
import mchorse.bbs.bridge.IBridgeHUD;
import mchorse.bbs.game.huds.HUDStage;

public class BridgeHUD extends BaseBridge implements IBridgeHUD
{
    public BridgeHUD(SandboxEngine engine)
    {
        super(engine);
    }

    @Override
    public HUDStage getHUDStage()
    {
        return this.engine.renderer.mainStage;
    }

    @Override
    public void replaceHUDStage(HUDStage stage)
    {
        this.engine.renderer.currentStage = stage;
    }
}