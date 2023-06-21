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

        this.fields = UI.scrollView(5, 6);
        this.fields.relative(this.content).full();

        this.content.add(this.fields);
    }
}