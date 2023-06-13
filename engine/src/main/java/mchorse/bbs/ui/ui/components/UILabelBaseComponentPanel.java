package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UILabelBaseComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.text.UITextarea;
import mchorse.bbs.ui.framework.elements.input.text.utils.TextLine;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;

public class UILabelBaseComponentPanel <T extends UILabelBaseComponent> extends UIComponentPanel<T>
{
    public UITextarea<TextLine> label;
    public UIColor color;
    public UIToggle textShadow;
    public UIToggle hasBackground;

    public UILabelBaseComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.label = new UITextarea<TextLine>((t) ->
        {
            this.component.label = t;
            this.panel.needsUpdate();
        });
        this.label.background().wrap().h(80);
        this.color = new UIColor((c) ->
        {
            this.component.color = c;
            this.panel.needsUpdate();
        });
        this.textShadow = new UIToggle(UIKeys.UI_COMPONENTS_LABEL_TEXT_SHADOW, (b) ->
        {
            this.component.textShadow = b.getValue();
            this.panel.needsUpdate();
        });
        this.hasBackground = new UIToggle(UIKeys.UI_COMPONENTS_LABEL_HAS_BACKGROUND, (b) ->
        {
            this.component.hasBackground = b.getValue();
            this.panel.needsUpdate();
        });

        this.prepend(this.hasBackground.marginBottom(8));
        this.prepend(this.textShadow);
        this.prepend(this.color);
        this.prepend(this.label);
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_LABEL_TITLE));
    }

    @Override
    public void fill(T component)
    {
        super.fill(component);

        this.label.setText(component.label);
        this.color.setColor(component.color);
        this.textShadow.setValue(component.textShadow);
    }
}