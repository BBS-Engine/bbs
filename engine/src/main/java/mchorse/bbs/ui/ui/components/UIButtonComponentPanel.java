package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UIButtonComponent;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;

public class UIButtonComponentPanel extends UILabelBaseComponentPanel<UIButtonComponent>
{
    public UIColor background;

    public UIButtonComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.background = new UIColor((c) ->
        {
            this.component.background = c;
            this.panel.needsUpdate();
        });

        this.prepend(this.background.marginBottom(8));
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_BUTTON_TITLE));
    }

    @Override
    public void fill(UIButtonComponent component)
    {
        super.fill(component);

        this.background.setColor(component.background);
    }
}