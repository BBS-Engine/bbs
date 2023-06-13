package mchorse.bbs.ui.camera.clips.renderer;

import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;

public interface IUIClipRenderer <T extends Clip>
{
    public void renderClip(UIContext context, T clip, Area area, boolean compact, boolean selected, boolean current);
}