package mchorse.bbs.game.scripts.ui.utils;

import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.ui.components.UIParentComponent;
import mchorse.bbs.ui.framework.elements.UIElement;

public class UIRootComponent extends UIParentComponent
{
    @Override
    protected UIElement subCreate(UserInterfaceContext context)
    {
        UIElement element = new UIElement();

        for (UIComponent component : this.getChildComponents())
        {
            UIElement created = component.create(context);

            created.relative(element);
            element.add(created);
        }

        return this.applyKeybinds(element, context);
    }
}