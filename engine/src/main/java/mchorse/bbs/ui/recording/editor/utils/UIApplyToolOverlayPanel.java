package mchorse.bbs.ui.recording.editor.utils;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageBarOverlayPanel;

import java.util.List;
import java.util.function.Consumer;

public class UIApplyToolOverlayPanel extends UIMessageBarOverlayPanel
{
    public UIStringList properties;
    public UIToggle relative;

    public Consumer<ApplyData> callback;

    public UIApplyToolOverlayPanel(IKey title, IKey message, Consumer<ApplyData> callback)
    {
        super(title, message);

        this.callback = callback;

        this.properties = new UIStringList(null);
        this.properties.relative(this.message).x(0.5F).y(1F, 5).w(1F).hTo(this.bar.area, -5).anchorX(0.5F);
        this.properties.add(Frame.PROPERTIES);
        this.properties.multi().setIndex(0);

        this.relative = new UIToggle(UIKeys.RECORD_EDITOR_TOOLS_APPLY_RELATIVE, null);
        this.relative.h(20);

        this.content.add(this.properties);
        this.bar.prepend(this.relative);
    }

    @Override
    public void confirm()
    {
        super.confirm();

        if (this.callback != null)
        {
            this.callback.accept(new ApplyData(this.properties.getCurrent(), this.relative.getValue()));
        }
    }

    public static class ApplyData
    {
        public List<String> properties;
        public boolean relative;

        public ApplyData(List<String> properties, boolean relative)
        {
            this.properties = properties;
            this.relative = relative;
        }
    }
}