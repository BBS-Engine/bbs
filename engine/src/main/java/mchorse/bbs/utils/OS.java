package mchorse.bbs.utils;

import java.util.Locale;

public enum OS
{
    WINDOWS, MACOS, LINUX;

    public static final OS CURRENT;

    static
    {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (os.contains("win"))
        {
            CURRENT = WINDOWS;
        }
        else if (os.contains("mac"))
        {
            CURRENT = MACOS;
        }
        else
        {
            /* Anything that isn't Windows or macOS is a Linux lmao */
            CURRENT = LINUX;
        }
    }
}