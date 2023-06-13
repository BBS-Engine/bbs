package mchorse.bbs.game.dialogues;

import mchorse.bbs.game.utils.factory.UIFactoryData;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;

public class DialogueFactoryData extends UIFactoryData<UIEventBaseNodePanel>
{
    public DialogueFactoryData(int color, Class<? extends UIEventBaseNodePanel> panelUI)
    {
        super(color, panelUI);
    }
}