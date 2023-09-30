package mchorse.bbs.bridge;

import mchorse.bbs.utils.recording.ScreenshotRecorder;
import mchorse.bbs.utils.recording.VideoRecorder;

public interface IBridgeVideoScreenshot
{
    public ScreenshotRecorder getScreenshotRecorder();

    public VideoRecorder getVideoRecorder();
}