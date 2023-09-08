package mchorse.bbs.ui.film.utils.keyframes;

import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

/**
 * Graph editor GUI designed specifically for keyframe fixture panel
 */
public class UICameraGraphEditor extends UICameraKeyframesEditor<UIGraphView>
{
    public UICameraGraphEditor(IUIClipsDelegate editor)
    {
        super(editor);

        this.keyframes.editor = editor;
    }

    @Override
    protected UIGraphView createElement()
    {
        return new UIGraphView(this, this::fillData);
    }

    public void setChannel(BaseValue value, int color)
    {
        KeyframeChannel keyframe = value instanceof KeyframeChannel ? (KeyframeChannel) value : null;

        if (keyframe == null)
        {
            throw new IllegalStateException("Given value doesn't have a keyframe channel! " + value.getClass().getSimpleName());
        }

        this.keyframes.clearSelection();
        this.keyframes.setChannel(keyframe, color);
        this.frameButtons.setVisible(false);
    }
}