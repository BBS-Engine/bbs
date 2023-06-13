package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UIToggleComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;

public class UIToggleComponentPanel extends UILabelBaseComponentPanel<UIToggleComponent>
{
    public UIToggle value;

    public UIToggleComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.value = new UIToggle(UIKeys.UI_COMPONENTS_TOGGLE_TOGGLED, (b) ->
        {
            this.component.state = b.getValue();
            this.panel.needsUpdate();
        });

        this.prepend(this.value.marginBottom(8));
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_TOGGLE_TITLE));
    }

    @Override
    public void fill(UIToggleComponent component)
    {
        super.fill(component);

        this.value.setValue(component.state);
    }
}