package mchorse.bbs;

import mchorse.bbs.animation.AnimationManager;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.CameraManager;
import mchorse.bbs.particles.ParticleManager;
import mchorse.bbs.recording.RecordManager;
import mchorse.bbs.recording.scene.SceneManager;

import java.io.File;

public class BBSData
{
    private static CameraManager cameras;
    private static SceneManager scenes;
    private static RecordManager records;
    private static AnimationManager animations;
    private static ParticleManager particles;

    public static CameraManager getCameras()
    {
        return cameras;
    }

    public static SceneManager getScenes()
    {
        return scenes;
    }

    public static RecordManager getRecords()
    {
        return records;
    }

    public static AnimationManager getAnimations()
    {
        return animations;
    }

    public static ParticleManager getParticles()
    {
        return particles;
    }

    public static void load(File folder, IBridge bridge)
    {
        cameras = new CameraManager(new File(folder, "cameras"));
        scenes = new SceneManager(new File(folder, "scenes"));
        records = new RecordManager(new File(folder, "records"), bridge);
        animations = new AnimationManager(new File(folder, "animations"));
        particles = new ParticleManager(new File(folder, "particles"));
    }

    public static void delete()
    {
        cameras = null;
        scenes = null;
        records = null;
        particles = null;
    }
}