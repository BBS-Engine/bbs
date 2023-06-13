package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UITextComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UITextComponentPanel extends UILabelBaseComponentPanel<UITextComponent>
{
    public UITrackpad anchor;

    public UITextComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.anchor = new UITrackpad((v) ->
        {
            this.component.textAnchor = v.floatValue();
            this.panel.needsUpdate();
        });
        this.anchor.limit(0F, 1F).tooltip(UIKeys.UI_COMPONENTS_TEXT_ANCHOR_TOOLTIP);

        this.prepend(this.anchor.marginBottom(8));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_TEXT_ANCHOR));
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_TEXT_TITLE));
    }

    @Override
    public void fill(UITextComponent component)
    {
        super.fill(component);

        this.anchor.setValue(component.textAnchor);
    }
}