package mchorse.bbs.ui.game.scripts.utils;

import mchorse.bbs.ui.framework.elements.overlay.UISoundOverlayPanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;

public class UIScriptSoundOverlayPanel extends UISoundOverlayPanel
{
    private UITextEditor editor;

    public UIScriptSoundOverlayPanel(UITextEditor editor)
    {
        super(null);

        this.editor = editor;
        this.set(editor.getSelectedText().replaceAll("\"", ""));
    }

    @Override
    public void onClose()
    {
        super.onClose();

        if (this.strings.list.isSelected() && this.strings.list.getIndex() > 0)
        {
            String current = this.editor.getSelectedText().trim();
            String result = this.strings.list.getCurrentFirst();

            if (current.startsWith("\""))
            {
                result = "\"" + result;
            }

            if (current.endsWith("\""))
            {
                result += "\"";
            }

            this.editor.pasteText(result);
        }
    }
}