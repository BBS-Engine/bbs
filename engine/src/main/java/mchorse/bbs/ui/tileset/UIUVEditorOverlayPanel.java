package mchorse.bbs.ui.tileset;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;

public class UIUVEditorOverlayPanel extends UIOverlayPanel
{
    public UIUVEditor uv;

    public UIUVEditorOverlayPanel(IKey title, Link atlas, Runnable callback)
    {
        super(title);

        this.uv = new UIUVEditor(atlas, callback);

        this.uv.relative(this).y(28).w(1F).h(1F, -28);
        this.add(this.uv);
    }
}