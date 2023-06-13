package mchorse.bbs.game.scripts.ui.utils;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;

public class UIKeybind implements IMapSerializable
{
    public int keyCode;
    public String action;
    public String label;
    public int modifier;

    public static int createModifier(boolean shift, boolean ctrl, boolean alt)
    {
        int modifier = shift ? 1 : 0;

        modifier += (ctrl ? 1 : 0) << 1;
        modifier += (alt ? 1 : 0) << 2;

        return modifier;
    }

    public UIKeybind()
    {}

    public UIKeybind(int keyCode, String action, String label, int modifier)
    {
        this.keyCode = keyCode;
        this.action = action;
        this.label = label;
        this.modifier = modifier;
    }

    public boolean isShift()
    {
        return (this.modifier & 0b1) == 1;
    }

    public boolean isCtrl()
    {
        return ((this.modifier >> 1) & 0b1) == 1;
    }

    public boolean isAlt()
    {
        return ((this.modifier >> 2) & 0b1) == 1;
    }

    @Override
    public void toData(MapType data)
    {
        data.putInt("keyCode", this.keyCode);
        data.putString("action", this.action);
        data.putString("label", this.label);
        data.putInt("modifier", this.modifier);
    }

    @Override
    public void fromData(MapType data)
    {
        this.keyCode = data.getInt("keyCode");
        this.action = data.getString("action");
        this.label = data.getString("label");
        this.modifier = data.getInt("modifier");
    }
}