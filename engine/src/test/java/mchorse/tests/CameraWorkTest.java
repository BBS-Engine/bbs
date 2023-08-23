package mchorse.tests;

import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.overwrite.IdleClip;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CameraWorkTest
{
    private CameraWork work;

    public CameraWorkTest()
    {
        IdleClip clip1 = new IdleClip();

        clip1.tick.set(60);
        clip1.duration.set(40);

        IdleClip clip2 = new IdleClip();

        clip2.layer.set(1);
        clip2.duration.set(50);

        IdleClip clip3 = new IdleClip();

        clip3.tick.set(110);
        clip3.duration.set(60);

        this.work = new CameraWork();
        this.work.clips.add(clip1);
        this.work.clips.add(clip2);
        this.work.clips.add(clip3);
    }

    @Test
    public void testTickFinding()
    {
        Assertions.assertEquals(100, this.work.clips.findNextTick(70));
        Assertions.assertEquals(60, this.work.clips.findPreviousTick(70));

        Assertions.assertEquals(50, this.work.clips.findPreviousTick(60));
        Assertions.assertEquals(110, this.work.clips.findNextTick(100));
    }
}
