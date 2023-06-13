package mchorse.bbs.ui.world.entities.components;

import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.world.entities.components.ItemComponent;

public class UIItemEntityComponent extends UIEntityComponent<ItemComponent>
{
    public UISlot item;

    public UIItemEntityComponent(ItemComponent component)
    {
        super(component);

        this.item = new UISlot(0, (s) -> this.component.stack = s.copy());
        this.item.setStack(component.stack.copy());

        this.add(this.item);
    }
}