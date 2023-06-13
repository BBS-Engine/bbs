package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageBarOverlayPanel;

import java.util.List;
import java.util.function.Consumer;

public class UIRecordOverlayPanel extends UIMessageBarOverlayPanel
{
    public UIStringList groups;

    private Consumer<List<String>> callback;

    public UIRecordOverlayPanel(IKey title, IKey message, Consumer<List<String>> callback)
    {
        super(title, message);

        this.callback = callback;

        this.groups = new UIStringList(null);
        this.groups.multi().add(Frame.GROUPS);
        this.groups.sort();

        this.groups.relative(this.content).x(0.5F).y(70).w(100).hTo(this.confirm.area, -5).anchorX(0.5F);

        this.content.add(this.groups);
    }

    @Override
    public void confirm()
    {
        super.confirm();

        if (this.callback != null)
        {
            this.callback.accept(this.groups.getCurrent());
        }
    }
}