package mchorse.bbs;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.film.FilmManager;
import mchorse.bbs.particles.ParticleManager;
import mchorse.bbs.screenplay.ScreenplayManager;

import java.io.File;

public class BBSData
{
    private static ParticleManager particles;
    private static ScreenplayManager screenplays;
    private static FilmManager films;

    public static ParticleManager getParticles()
    {
        return particles;
    }

    public static ScreenplayManager getScreenplays()
    {
        return screenplays;
    }

    public static FilmManager getFilms()
    {
        return films;
    }

    public static void load(File folder, IBridge bridge)
    {
        particles = new ParticleManager(new File(folder, "particles"));
        screenplays = new ScreenplayManager(new File(folder, "screenplays"));
        films = new FilmManager(new File(folder, "films"));
    }

    public static void delete()
    {
        particles = null;
        screenplays = null;
        films = null;
    }
}