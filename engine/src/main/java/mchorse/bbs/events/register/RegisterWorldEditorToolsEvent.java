package mchorse.bbs.events.register;

import mchorse.bbs.ui.world.tools.UITool;

import java.util.List;

public class RegisterWorldEditorToolsEvent
{
    public final List<UITool> tools;

    public RegisterWorldEditorToolsEvent(List<UITool> tools)
    {
        this.tools = tools;
    }

    public void registerTool(UITool tool)
    {
        this.tools.add(tool);
    }
}