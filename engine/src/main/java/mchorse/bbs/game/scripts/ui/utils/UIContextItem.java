package mchorse.bbs.game.scripts.ui.utils;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;

public class UIContextItem implements IMapSerializable
{
    public String icon = "";
    public String action = "";
    public String label = "";
    public int color;

    public UIContextItem()
    {}

    public UIContextItem(String icon, String action, String label, int color)
    {
        this.icon = icon;
        this.action = action;
        this.label = label;
        this.color = color;
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("icon", this.icon);
        data.putString("action", this.action);
        data.putString("label", this.label);
        data.putInt("color", this.color);
    }

    @Override
    public void fromData(MapType data)
    {
        this.icon = data.getString("icon");
        this.action = data.getString("action");
        this.label = data.getString("label");
        this.color = data.getInt("color");
    }
}