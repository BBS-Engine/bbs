package mchorse;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.JavaLauncher;

import java.io.File;
import java.util.List;

/**
 * Launcher.
 *
 * This class is responsible for launching the game with all of its dependencies,
 * in a user-friendly manner (i.e. by double-clicking on the jar).
 */
public class Launcher
{
    public static void main(String[] strings)
    {
        JavaLauncher launcher = new JavaLauncher();
        MapType defaultSettings = new MapType();

        defaultSettings.putInt("game.width", 1280);
        defaultSettings.putInt("game.height", 720);
        defaultSettings.putString("game.world", "hello");
        defaultSettings.putBool("game.development", true);
        defaultSettings.putString("game.directory", "game");

        List<String> args = launcher.getArguments("net.fabricmc.loader.impl.launch.knot.KnotClient");
        MapType settings = launcher.readSettings(new File("launcher.json"), defaultSettings);

        System.out.println(settings);

        String gameDirectory = settings.getString("game.directory");

        args.add("--gameDirectory");
        args.add(gameDirectory);

        if (settings.has("game.world"))
        {
            args.add("-dw");
            args.add(settings.getString("game.world"));
        }

        if (settings.has("game.width"))
        {
            args.add("-ww");
            args.add(String.valueOf(settings.getInt("game.width")));
        }

        if (settings.has("game.height"))
        {
            args.add("-wh");
            args.add(String.valueOf(settings.getInt("game.height")));
        }

        if (settings.getBool("game.development"))
        {
            args.add("--development");
        }

        try
        {
            File logFile = launcher.getLogFile(gameDirectory);

            launcher.launch(args, logFile);

            System.out.println(String.join(" ", args));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}