package mchorse.bbs.game.utils.factory;

import mchorse.bbs.utils.colors.Colors;

public class UIFactoryData <T>
{
    public final int color;
    public final Class<? extends T> panelUI;

    public UIFactoryData(int color, Class<? extends T> panelUI)
    {
        this.color = color & Colors.RGB;
        this.panelUI = panelUI;
    }
}