package mchorse;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.CrashReport;
import mchorse.bbs.utils.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
        List<String> args = new ArrayList<String>();
        MapType settings = readSettings();
        String java = new File(System.getProperty("java.home"), "bin/java").getAbsolutePath();
        String gameDirectory = settings.getString("game.directory");

        args.add(java);

        if (OS.CURRENT == OS.MACOS)
        {
            args.add("-XstartOnFirstThread");
        }

        args.add("-Dfile.encoding=UTF-8");
        args.add("-classpath");
        args.add(getClasspath());
        args.add("net.fabricmc.loader.impl.launch.knot.KnotClient");
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
            ProcessBuilder process = new ProcessBuilder(args);
            File logs = new File(gameDirectory, "logs/launcher.log");

            if (!logs.getParentFile().isDirectory())
            {
                logs.getParentFile().mkdirs();
            }

            if (logs.isFile())
            {
                Path path = logs.toPath();
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                String name = CrashReport.FORMATTER.format(attributes.lastModifiedTime().toInstant()) + ".log";

                logs.renameTo(new File(gameDirectory, "logs/" + name));
            }

            process.redirectErrorStream(true);
            process.redirectOutput(logs);
            process.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        System.out.println(String.join(" ", args));
    }

    private static MapType readSettings()
    {
        File launcher = new File("launcher.json");

        try
        {
            return (MapType) DataToString.read(launcher);
        }
        catch (IOException e)
        {}

        MapType map = new MapType();

        map.putInt("game.width", 1280);
        map.putInt("game.height", 720);
        map.putString("game.world", "hello");
        map.putBool("game.development", true);
        map.putString("game.directory", "game");

        DataToString.writeSilently(launcher, map, true);

        return map;
    }

    private static String getClasspath()
    {
        File folder = new File(System.getProperty("user.dir"));
        StringJoiner joiner = new StringJoiner(File.pathSeparator);
        String slash = File.separator;

        /* Nashorn (for scripting) */
        joiner.add(System.getProperty("java.home") + slash + "lib" + slash + "ext" + slash + "nashorn.jar");

        for (File file : folder.listFiles())
        {
            String name = file.getName();

            if (name.equals("launcher.jar"))
            {
                joiner.add(name);
            }
            else if (name.equals("dependencies") && file.isDirectory())
            {
                for (File dep : file.listFiles())
                {
                    if (dep.getName().endsWith(".jar"))
                    {
                        joiner.add(new File("dependencies" + slash + dep.getName()).getAbsolutePath());
                    }
                }
            }
        }

        return joiner.toString();
    }
}