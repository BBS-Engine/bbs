package mchorse.bbs.ui.film.utils.keyframes;

import mchorse.bbs.camera.clips.overwrite.KeyframeClip;
import mchorse.bbs.camera.values.ValueKeyframeChannel;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
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

        for (int i = 0; i < clip.channels.length; i++)
        {
            sheets.add(new UISheet(String.valueOf(i), TITLES[i + 1], COLORS[i], clip.channels[i].get()));
        }

        this.frameButtons.setVisible(false);
    }

    public void setChannels(List<ValueKeyframeChannel> channels, List<Integer> colors)
    {
        List<UISheet> sheets = this.keyframes.sheets;

        sheets.clear();
        this.keyframes.clearSelection();

        for (int i = 0; i < channels.size(); i++)
        {
            ValueKeyframeChannel channel = channels.get(i);

            sheets.add(new UISheet(channel.getId(), IKey.raw(channel.getId()), colors.get(i), channel.get()));
        }

        this.frameButtons.setVisible(false);
    }
}