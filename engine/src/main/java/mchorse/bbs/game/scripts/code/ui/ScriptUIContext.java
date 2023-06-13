package mchorse.bbs.game.scripts.code.ui;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.user.ui.IScriptUIContext;

public class ScriptUIContext implements IScriptUIContext
{
    private UserInterfaceContext context;

    public ScriptUIContext(UserInterfaceContext context)
    {
        this.context = context;
    }

    @Override
    public MapType getData()
    {
        return this.context.data;
    }

    @Override
    public boolean isClosed()
    {
        return this.context.isClosed();
    }

    @Override
    public String getLast()
    {
        return this.context.getLast();
    }

    @Override
    public String getHotkey()
    {
        return this.context.getHotkey();
    }

    @Override
    public String getContext()
    {
        return this.context.getContext();
    }

    @Override
    public UIComponent get(String id)
    {
        return this.context.getById(id);
    }

    @Override
    public void sendToPlayer()
    {
        this.context.sendToPlayer();
    }
}