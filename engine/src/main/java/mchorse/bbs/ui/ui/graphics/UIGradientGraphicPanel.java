package mchorse.bbs.ui.ui.graphics;

import mchorse.bbs.game.scripts.ui.graphics.GradientGraphic;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UIGradientGraphicPanel extends UIGraphicPanel<GradientGraphic>
{
    public UIColor secondary;
    public UIToggle horizontal;

    public UIGradientGraphicPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.secondary = new UIColor((c) -> this.graphic.secondary = c).withAlpha();
        this.horizontal = new UIToggle(UIKeys.UI_GRAPHICS_HORIZONTAL, (b) -> this.graphic.horizontal = b.getValue());

        this.add(UI.label(UIKeys.UI_GRAPHICS_SECONDARY));
        this.add(this.secondary);
        this.add(this.horizontal);
    }

    @Override
    public void fill(GradientGraphic graphic)
    {
        super.fill(graphic);

        this.secondary.setColor(graphic.secondary);
        this.horizontal.setValue(graphic.horizontal);
    }
}