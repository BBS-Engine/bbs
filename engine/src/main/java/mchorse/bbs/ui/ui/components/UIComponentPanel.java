package mchorse.bbs.ui.ui.components;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.ui.UIUIUnit;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;

public class UIComponentPanel <T extends UIComponent> extends UIScrollView
{
    public UITextbox id;
    public UITextbox tooltip;
    public UIToggle visible;
    public UIToggle enabled;

    public UITrackpad marginTop;
    public UITrackpad marginBottom;
    public UITrackpad marginLeft;
    public UITrackpad marginRight;

    public UIUIUnit x;
    public UIUIUnit y;
    public UIUIUnit w;
    public UIUIUnit h;

    public UITrackpad updateDelay;

    protected UIUserInterfacePanel panel;
    protected T component;

    public UIComponentPanel(UIUserInterfacePanel panel)
    {
        this.panel = panel;

        this.column().vertical().scroll().stretch().padding(10);

        this.id = new UITextbox((t) ->
        {
            this.component.id = t;
            this.panel.needsUpdate();
        });

        this.tooltip = new UITextbox((t) ->
        {
            this.component.tooltip = t;
            this.panel.needsUpdate();
        });

        this.visible = new UIToggle(UIKeys.UI_COMPONENTS_GENERAL_VISIBLE, (b) ->
        {
            this.component.visible = b.getValue();
            this.panel.needsUpdate();
        });

        this.enabled = new UIToggle(UIKeys.UI_COMPONENTS_GENERAL_ENABLED, (b) ->
        {
            this.component.enabled = b.getValue();
            this.panel.needsUpdate();
        });

        this.marginTop = new UITrackpad((v) ->
        {
            this.component.marginTop = v.intValue();
            this.panel.needsUpdate();
        }).integer();
        this.marginTop.tooltip(UIKeys.ENGINE_DIRECTION_TOP);

        this.marginBottom = new UITrackpad((v) ->
        {
            this.component.marginBottom = v.intValue();
            this.panel.needsUpdate();
        }).integer();
        this.marginBottom.tooltip(UIKeys.ENGINE_DIRECTION_BOTTOM);

        this.marginLeft = new UITrackpad((v) ->
        {
            this.component.marginLeft = v.intValue();
            this.panel.needsUpdate();
        }).integer();
        this.marginLeft.tooltip(UIKeys.ENGINE_DIRECTION_LEFT);

        this.marginRight = new UITrackpad((v) ->
        {
            this.component.marginRight = v.intValue();
            this.panel.needsUpdate();
        }).integer();
        this.marginRight.tooltip(UIKeys.ENGINE_DIRECTION_RIGHT);

        this.x = new UIUIUnit(UIKeys.X,  (v) -> this.panel.needsUpdate());
        this.y = new UIUIUnit(UIKeys.Y, (v) -> this.panel.needsUpdate());
        this.w = new UIUIUnit(UIKeys.UI_COMPONENTS_GENERAL_WIDTH, (v) -> this.panel.needsUpdate());
        this.h = new UIUIUnit(UIKeys.UI_COMPONENTS_GENERAL_HEIGHT, (v) -> this.panel.needsUpdate());

        this.updateDelay = new UITrackpad((v) ->
        {
            this.component.updateDelay = v.intValue();
            this.panel.needsUpdate();
        }).integer().limit(0);

        this.add(createSectionLabel(UIKeys.UI_COMPONENTS_GENERAL_TITLE));
        this.add(UI.label(UIKeys.UI_COMPONENTS_GENERAL_ID), this.id);
        this.add(UI.label(UIKeys.UI_COMPONENTS_GENERAL_TOOLTIP), this.tooltip);
        this.add(this.enabled, this.visible);
        this.add(
            UI.label(UIKeys.UI_COMPONENTS_GENERAL_MARGIN).marginTop(8),
            UI.row(this.marginTop, this.marginBottom, this.marginLeft, this.marginRight)
        );
        this.add(this.x.marginTop(8));
        this.add(this.y);
        this.add(this.w);
        this.add(this.h);
        this.add(UI.label(UIKeys.UI_COMPONENTS_GENERAL_UPDATE_DELAY).marginTop(8), this.updateDelay);
    }

    protected UILabel createSectionLabel(IKey label)
    {
        UILabel uiLabel = UI.label(label).background(() -> BBSSettings.primaryColor(Colors.A100));

        uiLabel.marginBottom(4);

        return uiLabel;
    }

    public void fill(T component)
    {
        this.component = component;

        this.id.setText(component.id);
        this.tooltip.setText(component.tooltip);
        this.enabled.setValue(component.enabled);
        this.visible.setValue(component.visible);

        this.marginTop.setValue(component.marginTop);
        this.marginBottom.setValue(component.marginBottom);
        this.marginLeft.setValue(component.marginLeft);
        this.marginRight.setValue(component.marginRight);

        this.x.fill(component.x);
        this.y.fill(component.y);
        this.w.fill(component.w);
        this.h.fill(component.h);

        this.updateDelay.setValue(component.updateDelay);
    }
}