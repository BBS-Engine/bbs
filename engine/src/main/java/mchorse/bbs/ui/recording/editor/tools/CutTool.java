package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.utils.icons.Icons;

public class CutTool extends Tool
{
    public CutTool()
    {
        super(Icons.CUT, Keys.RECORD_TOOL_CUT);
    }

    @Override
    public boolean apply(ToolContext context)
    {
        if (new CopyTool().apply(context))
        {
            return new RemoveTool().apply(context);
        }

        return false;
    }
}