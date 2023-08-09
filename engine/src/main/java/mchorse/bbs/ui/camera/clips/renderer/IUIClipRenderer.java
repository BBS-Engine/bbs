package mchorse.bbs.ui.camera.clips.renderer;

import mchorse.bbs.ui.camera.UIClips;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;

public interface IUIClipRenderer <T extends Clip>
{
    public void renderClip(UIContext context, UIClips clips, T clip, Area area, boolean selected, boolean current);
}