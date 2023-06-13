package mchorse.bbs.camera.utils;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.StringUtils;

public class TimeUtils
{
    public static String formatTime(long ticks)
    {
        if (BBSSettings.editorSeconds.get())
        {
            long seconds = (long) (ticks / 20D);
            int milliseconds = (int) (ticks % 20 == 0 ? 0 : ticks % 20 * 5D);

            return seconds + "." + StringUtils.leftPad(String.valueOf(milliseconds), 2, "0");
        }

        return String.valueOf(ticks);
    }

    public static double toTime(int ticks)
    {
        return BBSSettings.editorSeconds.get() ? ticks / 20D : ticks;
    }

    public static int fromTime(double time)
    {
        return BBSSettings.editorSeconds.get() ? (int) Math.round(time * 20) : (int) time;
    }

    public static void configure(UITrackpad element, int defaultValue)
    {
        if (BBSSettings.editorSeconds.get())
        {
            element.values(0.1D, 0.05D, 0.25D).limit(defaultValue / 20D, Double.POSITIVE_INFINITY, false);
        }
        else
        {
            element.values(1.0D).limit(defaultValue, Double.POSITIVE_INFINITY, true);
        }
    }
}