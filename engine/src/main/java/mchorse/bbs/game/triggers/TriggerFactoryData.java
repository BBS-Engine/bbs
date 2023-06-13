package mchorse.bbs.game.triggers;

import mchorse.bbs.game.utils.factory.UIFactoryData;
import mchorse.bbs.ui.game.triggers.panels.UITriggerBlockPanel;

public class TriggerFactoryData extends UIFactoryData<UITriggerBlockPanel>
{
    public TriggerFactoryData(int color, Class<? extends UITriggerBlockPanel> panelUI)
    {
        super(color, panelUI);
    }
}