package mchorse.bbs.ui.utils;

import mchorse.bbs.BBS;
import mchorse.bbs.audio.SoundPlayer;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.utils.OS;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;

public class UIUtils
{
    /**
     * Open web link (in default web browser)
     */
    public static boolean openWebLink(String address)
    {
        if (OS.CURRENT == OS.WINDOWS)
        {
            return runSysCommand("rundll32", "url.dll,FileProtocolHandler", address);
        }
        else if (OS.CURRENT == OS.MACOS)
        {
            return runSysCommand("open", address);
        }

        return runSysCommand("kde-open", address)
            || runSysCommand("gnome-open", address)
            || runSysCommand("xdg-open", address);
    }

    /**
     * Open a folder (in default file browser)
     */
    public static boolean openFolder(File folder)
    {
        try
        {
            String path = folder.getAbsolutePath();

            if (OS.CURRENT == OS.WINDOWS)
            {
                return runSysCommand("explorer", path);
            }
            else if (OS.CURRENT == OS.MACOS)
            {
                return runSysCommand("open", path);
            }

            return runSysCommand("kde-open", path)
                || runSysCommand("gnome-open", path)
                || runSysCommand("xdg-open", path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private static boolean runSysCommand(String... command)
    {
        try
        {
            Process p = Runtime.getRuntime().exec(command);

            if (p == null)
            {
                return false;
            }

            try
            {
                return p.exitValue() == 0;
            }
            catch (IllegalThreadStateException e)
            {
                return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return false;
        }
    }

    public static void playClick()
    {
        playClick(1F);
    }

    public static void playClick(float pitch)
    {
        SoundPlayer player = BBS.getSounds().play(Link.assets("sounds/click.ogg"));

        if (player != null)
        {
            player.setRelative(true);
            player.setPosition(0, 0, 0);
            player.setVolume(1F);
            player.setPitch(pitch);
            player.play();
        }
    }

    public static KeyCombo createCombo(IKey label, int base, int index)
    {
        int keys[] = new int[index > 9 ? 2 : 1];

        keys[0] = base + (index % 10);

        if (index >= 20)
        {
            keys[1] = GLFW.GLFW_KEY_LEFT_ALT;
        }
        else if (index >= 10)
        {
            keys[1] = GLFW.GLFW_KEY_LEFT_SHIFT;
        }

        return new KeyCombo(label, keys);
    }
}