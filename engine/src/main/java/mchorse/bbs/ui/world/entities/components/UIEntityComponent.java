package mchorse.bbs.ui.world.entities.components;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.world.entities.components.Component;

public class UIEntityComponent <T extends Component> extends UIElement
{
    public T component;

    public UIEntityComponent(T component)
    {
        super();

        this.component = component;

        this.column().vertical().stretch();

        this.add(UI.label(UIKeys.C_ENTITY_COMPONENT.get(component.getId())).background());
    }
}