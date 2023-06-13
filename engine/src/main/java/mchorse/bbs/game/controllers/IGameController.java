package mchorse.bbs.game.controllers;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.core.IRenderable;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.core.input.IJoystickHandler;
import mchorse.bbs.core.input.IKeyHandler;
import mchorse.bbs.core.input.IMouseHandler;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.world.entities.Entity;

public interface IGameController extends ITickable, IMouseHandler, IKeyHandler, IJoystickHandler, IRenderable, IMapSerializable
{
    public void initilize(IBridge bridge);

    public boolean canControl();

    public void enable();

    public void disable();

    public void reset();

    public void renderHUD(UIRenderingContext context, int w, int h);

    public void renderInWorld(RenderingContext context);

    public boolean canRenderEntity(Entity entity, RenderingContext context);
}