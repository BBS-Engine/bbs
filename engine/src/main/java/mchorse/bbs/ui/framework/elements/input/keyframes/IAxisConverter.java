package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.keyframes.Keyframe;

public interface IAxisConverter
{
    public String format(double value);

    public double from(double x);

    public double to(double x);

    public void updateField(UITrackpad element);

    public boolean forceInteger(Keyframe keyframe, Selection selection, boolean forceInteger);
}