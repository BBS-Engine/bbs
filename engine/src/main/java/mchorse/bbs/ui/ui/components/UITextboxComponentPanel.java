package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UITextboxComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UITextboxComponentPanel extends UILabelBaseComponentPanel<UITextboxComponent>
{
    public UITrackpad maxLength;

    public UITextboxComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.maxLength = new UITrackpad((c) ->
        {
            this.component.maxLength = c.intValue();
            this.panel.needsUpdate();
        });
        this.maxLength.limit(1).integer();

        this.prepend(this.maxLength.marginBottom(8));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_TEXTBOX_MAX_LENGTH));
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_TEXTBOX_TITLE));
    }

    @Override
    public void fill(UITextboxComponent component)
    {
        super.fill(component);

        this.maxLength.setValue(component.maxLength);
    }
}