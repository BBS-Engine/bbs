package mchorse.bbs.recording.actions;

import mchorse.bbs.game.utils.factory.UIFactoryData;
import mchorse.bbs.ui.recording.editor.actions.UIActionPanel;

public class ActionFactoryData extends UIFactoryData<UIActionPanel>
{
    public ActionFactoryData(int color, Class<? extends UIActionPanel> panelUI)
    {
        super(color, panelUI);
    }
}