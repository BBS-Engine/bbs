package mchorse.bbs.utils;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Basic launcher that is responsible for constructing arguments for
 * process builder, and launch the actual game/app with needed classpath
 * JVM and program arguments.
 */
public class JavaLauncher
{
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

    public List<String> getArguments(String mainClass)
    {
        List<String> args = new ArrayList<>();
        String java = new File(System.getProperty("java.home"), "bin/java").getAbsolutePath();

        args.add(java);

        if (OS.CURRENT == OS.MACOS)
        {
            args.add("-XstartOnFirstThread");
        }

        args.add("-Dfile.encoding=UTF-8");
        args.add("-classpath");
        args.add(getClasspath());
        args.add(mainClass);

        return args;
    }

    public File getLogFile(String gameDirectory) throws IOException
    {
        File log = new File(gameDirectory, "logs/launcher.log");

        if (!log.getParentFile().isDirectory())
        {
            log.getParentFile().mkdirs();
        }

        if (log.isFile())
        {
            Path path = log.toPath();
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            String name = CrashReport.FORMATTER.format(attributes.lastModifiedTime().toInstant()) + ".log";

            log.renameTo(new File(gameDirectory, "logs/" + name));
        }

        return log;
    }

    public void launch(List<String> args, File logs) throws IOException
    {
        ProcessBuilder process = new ProcessBuilder(args);

        process.redirectErrorStream(true);
        process.redirectOutput(logs);
        process.start();
    }

    public MapType readSettings(File launcher, MapType defaultSettings)
    {
        try
        {
            BaseType data = DataToString.read(launcher);

            if (data instanceof MapType)
            {
                return (MapType) data;
            }
        }
        catch (Exception e)
        {}

        DataToString.writeSilently(launcher, defaultSettings, true);

        return defaultSettings;
    }
}