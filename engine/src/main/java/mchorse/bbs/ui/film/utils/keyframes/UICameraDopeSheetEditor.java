package mchorse.bbs.ui.film.utils.keyframes;

import mchorse.bbs.camera.clips.overwrite.KeyframeClip;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.utils.CameraAxisConverter;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.UISheet;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

import java.util.List;

public class UICameraDopeSheetEditor extends UIKeyframesEditor<UIDopeSheetView>
{
    public static final IKey[] TITLES = {UIKeys.CAMERA_PANELS_ALL, UIKeys.X, UIKeys.Y, UIKeys.Z, UIKeys.CAMERA_PANELS_YAW, UIKeys.CAMERA_PANELS_PITCH, UIKeys.CAMERA_PANELS_ROLL, UIKeys.CAMERA_PANELS_FOV};
    public static final int[] COLORS = {Colors.RED, Colors.GREEN, Colors.BLUE, Colors.CYAN, Colors.MAGENTA, Colors.YELLOW, Colors.LIGHTEST_GRAY};
    public static final CameraAxisConverter CONVERTER = new CameraAxisConverter();

    protected IUIClipsDelegate editor;

    public UICameraDopeSheetEditor(IUIClipsDelegate editor)
    {
        super();

        this.editor = editor;
        this.keyframes.editor = editor;
    }

    public void updateConverter()
    {
        this.setConverter(CONVERTER);
    }

    @Override
    protected UIDopeSheetView createElement()
    {
        return new UIDopeSheetView(this, this::fillData);
    }

    public void setChannel(KeyframeChannel channel, int color)
    {
        List<UISheet> sheets = this.keyframes.sheets;

        sheets.clear();
        this.keyframes.clearSelection();

        sheets.add(new UISheet(channel.getId(), IKey.raw(channel.getId()), color, channel));

        this.frameButtons.setVisible(false);
    }

    public void setClip(KeyframeClip clip)
    {
        List<UISheet> sheets = this.keyframes.sheets;

        sheets.clear();
        this.keyframes.clearSelection();

        for (int i = 0; i < clip.channels.length; i++)
        {
            KeyframeChannel channel = clip.channels[i];

            sheets.add(new UISheet(channel.getId(), IKey.raw(channel.getId()), COLORS[i], channel));
        }

        this.frameButtons.setVisible(false);
    }

    public void setChannels(List<KeyframeChannel> channels, List<Integer> colors)
    {
        List<UISheet> sheets = this.keyframes.sheets;

        sheets.clear();
        this.keyframes.clearSelection();

        for (int i = 0; i < channels.size(); i++)
        {
            KeyframeChannel channel = channels.get(i);

            sheets.add(new UISheet(channel.getId(), IKey.raw(channel.getId()), colors.get(i), channel));
        }

        this.frameButtons.setVisible(false);
    }
}