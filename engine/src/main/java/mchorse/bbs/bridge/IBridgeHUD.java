package mchorse.bbs.bridge;

import mchorse.bbs.animation.Animations;
import mchorse.bbs.game.huds.HUDStage;

public interface IBridgeHUD
{
    public HUDStage getHUDStage();

    public void replaceHUDStage(HUDStage stage);
}