package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UILabelComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UILabelComponentPanel extends UILabelBaseComponentPanel<UILabelComponent>
{
    public UIColor background;
    public UITrackpad anchorX;
    public UITrackpad anchorY;

    public UILabelComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.background = new UIColor((c) ->
        {
            this.component.background = c;
            this.panel.needsUpdate();
        }).withAlpha();

        this.anchorX = new UITrackpad((v) ->
        {
            this.component.anchorX = v.floatValue();
            this.panel.needsUpdate();
        }).limit(0F, 1F);

        this.anchorY = new UITrackpad((v) ->
        {
            this.component.anchorY = v.floatValue();
            this.panel.needsUpdate();
        }).limit(0F, 1F);

        this.prepend(UI.row(this.anchorX, this.anchorY).marginBottom(8));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_TEXT_ANCHOR));
        this.prepend(this.background);
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_TEXT_TITLE));
    }

    @Override
    public void fill(UILabelComponent component)
    {
        super.fill(component);

        this.background.setColor(component.background == null ? 0 : component.background);
        this.anchorX.setValue(component.anchorX);
        this.anchorY.setValue(component.anchorY);
    }
}