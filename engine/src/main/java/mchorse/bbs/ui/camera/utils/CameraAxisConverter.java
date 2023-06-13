package mchorse.bbs.ui.camera.utils;

import mchorse.bbs.camera.utils.TimeUtils;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.keyframes.IAxisConverter;
import mchorse.bbs.ui.framework.elements.input.keyframes.Selection;
import mchorse.bbs.utils.keyframes.Keyframe;

public class CameraAxisConverter implements IAxisConverter
{
    @Override
    public String format(double value)
    {
        return TimeUtils.formatTime((long) value);
    }

    @Override
    public double from(double v)
    {
        return TimeUtils.fromTime(v);
    }

    @Override
    public double to(double v)
    {
        return TimeUtils.toTime((int) v);
    }

    @Override
    public void updateField(UITrackpad element)
    {
        TimeUtils.configure(element, 0);

        element.limit(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public boolean forceInteger(Keyframe keyframe, Selection selection, boolean forceInteger)
    {
        return !BBSSettings.editorSeconds.get() && forceInteger;
    }
}