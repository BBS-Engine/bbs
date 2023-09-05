package mchorse.bbs.utils;

import mchorse.bbs.BBSSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FFMpegUtils
{
    public static boolean execute(File folder, String... arguments)
    {
        List<String> args = new ArrayList<String>();

        args.add(BBSSettings.videoEncoderPath.get());

        for (String arg : arguments)
        {
            args.add(arg);
        }

        ProcessBuilder builder = new ProcessBuilder(args);

        builder.directory(folder);

        try
        {
            Process start = builder.start();

            return start.waitFor() == 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}