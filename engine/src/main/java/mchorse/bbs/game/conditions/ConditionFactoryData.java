package mchorse.bbs.game.conditions;

import mchorse.bbs.game.utils.factory.UIFactoryData;
import mchorse.bbs.ui.game.conditions.blocks.UIConditionBlockPanel;

public class ConditionFactoryData extends UIFactoryData<UIConditionBlockPanel>
{
    public ConditionFactoryData(int color, Class<? extends UIConditionBlockPanel> panelUI)
    {
        super(color, panelUI);
    }
}