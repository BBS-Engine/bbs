package mchorse.bbs.camera;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.values.ValueClips;

public class CameraWork extends StructureBase
{
    public ValueClips clips = new ValueClips("clips", BBS.getFactoryClips());

    public CameraWork()
    {
        this.register(this.clips);
    }
}