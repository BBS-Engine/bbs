package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UITrackpadComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UITrackpadComponentPanel extends UIComponentPanel<UITrackpadComponent>
{
    public UITrackpad value;
    public UITrackpad min;
    public UITrackpad max;
    public UIToggle integer;

    public UITrackpad normal;
    public UITrackpad weak;
    public UITrackpad strong;
    public UITrackpad increment;

    public UITrackpadComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.value = new UITrackpad((v) ->
        {
            this.component.value = v;
            this.panel.needsUpdate();
        });

        this.min = new UITrackpad((v) ->
        {
            this.component.min = v;
            this.panel.needsUpdate();
        });
        this.min.tooltip(UIKeys.UI_COMPONENTS_TRACKPAD_MIN);

        this.max = new UITrackpad((v) ->
        {
            this.component.max = v;
            this.panel.needsUpdate();
        });
        this.max.tooltip(UIKeys.UI_COMPONENTS_TRACKPAD_MAX);

        this.integer = new UIToggle(UIKeys.UI_COMPONENTS_TRACKPAD_INTEGER, (b) ->
        {
            this.component.integer = b.getValue();
            this.panel.needsUpdate();
        });

        this.normal = new UITrackpad((v) ->
        {
            this.component.normal = v;
            this.panel.needsUpdate();
        });

        this.weak = new UITrackpad((v) ->
        {
            this.component.weak = v;
            this.panel.needsUpdate();
        });

        this.strong = new UITrackpad((v) ->
        {
            this.component.strong = v;
            this.panel.needsUpdate();
        });

        this.increment = new UITrackpad((v) ->
        {
            this.component.increment = v;
            this.panel.needsUpdate();
        });

        this.prepend(this.increment.marginBottom(8));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_TRACKPAD_INCREMENT_ARROWS).marginTop(4));
        this.prepend(UI.row(this.weak, this.normal, this.strong));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_TRACKPAD_INCREMENT).marginTop(4));
        this.prepend(this.integer);
        this.prepend(UI.row(this.min, this.max));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_TRACKPAD_RANGE).marginTop(4));
        this.prepend(this.value);
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_TRACKPAD_VALUE));
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_TRACKPAD_TITLE));
    }

    @Override
    public void fill(UITrackpadComponent component)
    {
        super.fill(component);

        this.value.setValue(component.value);
        this.min.setValue(component.min);
        this.max.setValue(component.max);
        this.integer.setValue(component.integer);
        this.normal.setValue(component.normal);
        this.weak.setValue(component.weak);
        this.strong.setValue(component.strong);
        this.increment.setValue(component.increment);
    }
}