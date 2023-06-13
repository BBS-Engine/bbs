package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UISlotComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;

public class UISlotComponentPanel extends UIComponentPanel<UISlotComponent>
{
    public UISlot stack;

    public UISlotComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.stack = new UISlot(0, (s) ->
        {
            this.component.stack = s.copy();
            this.panel.needsUpdate();
        });

        this.prepend(this.stack.marginBottom(8));
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_SLOT_TITLE));
    }

    @Override
    public void fill(UISlotComponent component)
    {
        super.fill(component);

        this.stack.setStack(component.stack);
    }
}