package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UIIconButtonComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.ui.utils.UIIconsOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIIconButtonComponentPanel extends UIComponentPanel<UIIconButtonComponent>
{
    public UIButton pickIcon;

    public UIIconButtonComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.pickIcon = new UIButton(UIKeys.UI_ICON_PICKER, (c) ->
        {
            UIIconsOverlayPanel overlayPanel = new UIIconsOverlayPanel(UIKeys.UI_ICON_PICKER_TITLE, (icon) ->
            {
                this.component.icon = icon == Icons.NONE ? "" : icon.id;
                this.panel.needsUpdate();
            });

            UIOverlay.addOverlay(this.getContext(), overlayPanel.set(this.component.icon));
        });

        this.prepend(this.pickIcon.marginBottom(8));
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_ICON_TITLE));
    }
}