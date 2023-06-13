package mchorse.bbs.game.scripts.ui.utils;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;

public class UIClick extends UIElement
{
    public UIComponent component;
    public UserInterfaceContext context;

    public UIClick(UIComponent component, UserInterfaceContext context)
    {
        super();

        this.component = component;
        this.context = context;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && !this.component.id.isEmpty())
        {
            ListType list = new ListType();

            list.addFloat(context.mouseX - this.area.x);
            list.addFloat(context.mouseY - this.area.y);
            list.addFloat((context.mouseX - this.area.x) / (float) this.area.w);
            list.addFloat((context.mouseY - this.area.y) / (float) this.area.h);
            list.addFloat(context.mouseButton);

            this.context.data.put(this.component.id, list);
            this.context.dirty(this.component.id, this.component.updateDelay);

            return true;
        }

        return super.subMouseClicked(context);
    }
}