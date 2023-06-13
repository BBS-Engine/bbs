package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UILayoutComponent;
import mchorse.bbs.game.scripts.ui.utils.LayoutType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UILayoutComponentPanel extends UIComponentPanel<UILayoutComponent>
{
    public UIToggle scroll;
    public UITrackpad scrollSize;
    public UIToggle horizontal;

    public UICirculate layoutType;
    public UITrackpad margin;
    public UITrackpad padding;

    public UITrackpad width;
    public UITrackpad items;

    public UILayoutComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.scroll = new UIToggle(UIKeys.UI_COMPONENTS_LAYOUT_SCROLLBARS, (b) ->
        {
            this.component.scroll = b.getValue();
            this.panel.needsUpdate();
        });

        this.scrollSize = new UITrackpad((v) ->
        {
            this.component.scrollSize = v.intValue();
            this.panel.needsUpdate();
        }).limit(0).integer();

        this.horizontal = new UIToggle(UIKeys.UI_COMPONENTS_LAYOUT_HORIZONTAL, (b) ->
        {
            this.component.horizontal = b.getValue();
            this.panel.needsUpdate();
        });

        this.layoutType = new UICirculate((b) ->
        {
            this.component.layoutType = LayoutType.values()[b.getValue()];
            this.panel.needsUpdate();
        });

        for (LayoutType type : LayoutType.values())
        {
            this.layoutType.addLabel(UIKeys.C_LAYOUT_TYPE.get(type));
        }

        this.margin = new UITrackpad((v) ->
        {
            this.component.margin = v.intValue();
            this.panel.needsUpdate();
        }).limit(0).integer();

        this.padding = new UITrackpad((v) ->
        {
            this.component.padding = v.intValue();
            this.panel.needsUpdate();
        }).limit(0).integer();

        this.width = new UITrackpad((v) ->
        {
            this.component.width = v.intValue();
            this.panel.needsUpdate();
        }).limit(0).integer();

        this.items = new UITrackpad((v) ->
        {
            this.component.items = v.intValue();
            this.panel.needsUpdate();
        }).limit(0).integer();

        this.prepend(this.items.marginBottom(8));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_LAYOUT_ITEMS));
        this.prepend(this.width);
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_LAYOUT_WIDTH));

        this.prepend(this.padding.marginBottom(8));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_LAYOUT_PADDING));
        this.prepend(this.margin);
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_GENERAL_MARGIN));
        this.prepend(this.layoutType);
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_LAYOUT_TYPE));

        this.prepend(this.horizontal.marginBottom(8));
        this.prepend(this.scrollSize);
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_LAYOUT_SCROLL_SIZE));
        this.prepend(this.scroll);
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_LAYOUT_TITLE));
    }

    @Override
    public void fill(UILayoutComponent component)
    {
        super.fill(component);

        this.scroll.setValue(component.scroll);
        this.scrollSize.setValue(component.scrollSize);
        this.horizontal.setValue(component.horizontal);

        this.layoutType.setValue(component.layoutType.ordinal());
        this.margin.setValue(component.margin);
        this.padding.setValue(component.padding);

        this.width.setValue(component.width);
        this.items.setValue(component.items);
    }
}