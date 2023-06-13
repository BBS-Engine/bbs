package mchorse.bbs.ui.ui.graphics;

import mchorse.bbs.game.scripts.ui.graphics.ShadowGraphic;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UIShadowGraphicPanel extends UIGraphicPanel<ShadowGraphic>
{
    public UIColor secondary;
    public UITrackpad offset;

    public UIShadowGraphicPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.secondary = new UIColor((c) -> this.graphic.secondary = c).withAlpha();
        this.offset = new UITrackpad((v) -> this.graphic.offset = v.intValue());

        this.add(UI.label(UIKeys.UI_GRAPHICS_SECONDARY), this.secondary, this.offset);
    }

    @Override
    public void fill(ShadowGraphic graphic)
    {
        super.fill(graphic);

        this.secondary.setColor(graphic.secondary);
        this.offset.setValue(graphic.offset);
    }
}