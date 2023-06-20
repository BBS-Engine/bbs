package mchorse.bbs.ui.dashboard.panels.overlay;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.UI;

public class UIOptionsOverlayPanel extends UIOverlayPanel
{
    public UIScrollView fields;

    public UIOptionsOverlayPanel()
    {
        super(UIKeys.PANELS_OPTIONS_TITLE);

        this.fields = UI.scrollView(5, 10);
        this.fields.relative(this.content).x(-10).w(1F, 20).h(1F);

        this.content.add(this.fields);
    }
}