package mchorse.bbs.game.scripts.ui.utils;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.resizers.Flex;

public class UIUnit implements IMapSerializable
{
    public int offset;
    public float value;
    public int max;
    public float anchor;
    public String target = "";
    public float targetAnchor;

    public void apply(Flex.Unit unit, UserInterfaceContext context)
    {
        UIElement target = context.getElement(this.target);

        unit.reset();
        unit.set(this.value, this.offset);

        unit.max = this.max;
        unit.anchor = this.anchor;
        unit.target = target == null ? null : target.getFlex();
        unit.targetAnchor = this.targetAnchor;
    }

    @Override
    public void toData(MapType data)
    {
        data.putInt("offset", this.offset);
        data.putFloat("value", this.value);
        data.putInt("max", this.max);
        data.putFloat("anchor", this.anchor);
        data.putString("target", this.target);
        data.putFloat("targetAnchor", this.targetAnchor);
    }

    @Override
    public void fromData(MapType data)
    {
        this.offset = data.getInt("offset");
        this.value = data.getFloat("value");
        this.max = data.getInt("max");
        this.anchor = data.getFloat("anchor");
        this.target = data.getString("target");
        this.targetAnchor = data.getFloat("targetAnchor");
    }
}