package mchorse.bbs.ui.ui.graphics;

import mchorse.bbs.game.scripts.ui.graphics.IconGraphic;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.ui.utils.UIIconsOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIIconGraphicPanel extends UIGraphicPanel<IconGraphic>
{
    public UIButton pickIcon;

    public UIIconGraphicPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.pickIcon = new UIButton(UIKeys.UI_ICON_PICKER, (b) ->
        {
            UIIconsOverlayPanel overlayPanel = new UIIconsOverlayPanel(UIKeys.UI_ICON_PICKER_TITLE, (icon) ->
            {
                this.graphic.id = icon == Icons.NONE ? "" : icon.id;
            });

            UIOverlay.addOverlay(this.getContext(), overlayPanel.set(this.graphic.id));
        });

        this.add(this.pickIcon);
    }
}
