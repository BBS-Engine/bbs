package mchorse.bbs.ui.recording.editor.utils;

import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.utils.Pair;

import java.util.function.Consumer;

public class UIProcessToolOverlayPanel extends UIPromptOverlayPanel
{
    public UIStringList properties;

    public UIProcessToolOverlayPanel(IKey title, IKey message, Consumer<Pair<String, String>> callback)
    {
        super(title, message, null);

        this.callback = (str) ->
        {
            if (callback != null)
            {
                callback.accept(new Pair<>(this.properties.getCurrentFirst(), str));
            }
        };

        this.properties = new UIStringList(null);
        this.properties.relative(this.message).x(0.5F).y(1F, 5).w(1F).hTo(this.bar.area, -5).anchorX(0.5F);
        this.properties.add(Frame.PROPERTIES);
        this.properties.setIndex(0);

        this.content.add(this.properties);
    }
}