package mchorse.bbs.ui.camera.utils;

import mchorse.bbs.camera.clips.overwrite.KeyframeClip;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.IUIClipsDelegate;
import mchorse.bbs.ui.framework.elements.input.keyframes.UISheet;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;

public class UICameraDopeSheetEditor extends UICameraKeyframesEditor<UIDopeSheetView>
{
    public static final IKey[] TITLES = {UIKeys.CAMERA_PANELS_ALL, UIKeys.X, UIKeys.Y, UIKeys.Z, UIKeys.CAMERA_PANELS_YAW, UIKeys.CAMERA_PANELS_PITCH, UIKeys.CAMERA_PANELS_ROLL, UIKeys.CAMERA_PANELS_FOV};
    public static final int[] COLORS = {Colors.RED, Colors.GREEN, Colors.BLUE, Colors.CYAN, Colors.MAGENTA, Colors.YELLOW, Colors.LIGHTEST_GRAY};

    public UICameraDopeSheetEditor(IUIClipsDelegate editor)
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

        this.valueChannels.clear();

        for (int i = 0; i < clip.channels.length; i++)
        {
            this.valueChannels.add(clip.channels[i]);
            sheets.add(new UISheet(String.valueOf(i), TITLES[i + 1], COLORS[i], clip.channels[i].get()));
        }

        this.frameButtons.setVisible(false);
    }
}