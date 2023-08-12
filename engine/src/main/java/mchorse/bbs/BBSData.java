package mchorse.bbs;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.CameraManager;
import mchorse.bbs.particles.ParticleManager;
import mchorse.bbs.recording.RecordManager;
import mchorse.bbs.recording.scene.SceneManager;
import mchorse.bbs.screenplay.ScreenplayManager;

import java.io.File;

public class BBSData
{
    private static CameraManager cameras;
    private static SceneManager scenes;
    private static RecordManager records;
    private static ParticleManager particles;
    private static ScreenplayManager screenplays;

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

    public static ParticleManager getParticles()
    {
        return particles;
    }

    public static ScreenplayManager getScreenplays()
    {
        return screenplays;
    }

    public static void load(File folder, IBridge bridge)
    {
        cameras = new CameraManager(new File(folder, "cameras"));
        scenes = new SceneManager(new File(folder, "scenes"));
        records = new RecordManager(new File(folder, "records"), bridge);
        particles = new ParticleManager(new File(folder, "particles"));
        screenplays = new ScreenplayManager(new File(folder, "screenplays"));
    }

    public static void delete()
    {
        cameras = null;
        scenes = null;
        records = null;
        particles = null;
        screenplays = null;
    }
}