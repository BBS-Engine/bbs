package mchorse.app.bridge;

import mchorse.app.GameEngine;

public class BaseBridge
{
    protected GameEngine engine;

    public BaseBridge(GameEngine engine)
    {
        this.engine = engine;
    }
}