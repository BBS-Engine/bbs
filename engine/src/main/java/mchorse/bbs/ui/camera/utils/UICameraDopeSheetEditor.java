package mchorse.bbs.ui.camera.utils;

import mchorse.bbs.camera.clips.overwrite.KeyframeClip;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.UIKeyframeClip;
import mchorse.bbs.ui.framework.elements.input.keyframes.UISheet;

import java.util.List;

public class UICameraDopeSheetEditor extends UICameraKeyframesEditor<UIDopeSheetView>
{
    public UICameraDopeSheetEditor(UICameraPanel editor)
    {
        super(editor);

        this.keyframes.editor = editor;
    }

    @Override
    protected UIDopeSheetView createElement()
    {
        return new UIDopeSheetView(this, this::fillData);
    }

    public void setClip(KeyframeClip clip)
    {
        List<UISheet> sheets = this.keyframes.sheets;

        sheets.clear();
        this.keyframes.clearSelection();

        if (this.editor.panel == null)
        {
            return;
        }

        UIKeyframeClip panel = (UIKeyframeClip) this.editor.panel;

        this.valueChannels.clear();

        for (int i = 0; i < clip.channels.length; i++)
        {
            this.valueChannels.add(clip.channels[i]);
            sheets.add(new UISheet(String.valueOf(i), panel.titles[i + 1], panel.colors[i], clip.channels[i].get()));
        }

        this.frameButtons.setVisible(false);
    }
}