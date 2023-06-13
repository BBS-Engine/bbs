package mchorse.bbs.settings;

import mchorse.bbs.utils.Timer;

import java.util.HashSet;
import java.util.Set;

/**
 * Config thread
 *
 * This bad boy is responsible for saving configs after some time
 */
public class SettingsThread implements Runnable
{
    private static SettingsThread instance;

    public Set<Settings> settings = new HashSet<Settings>();
    public Timer timer = new Timer(2000);

    public static synchronized void add(Settings settings)
    {
        if (instance == null)
        {
            instance = new SettingsThread();
            instance.addConfig(settings);
            new Thread(instance).start();
        }
        else
        {
            instance.addConfig(settings);
        }
    }

    public void addConfig(Settings settings)
    {
        this.settings.add(settings);
        this.timer.mark();
    }

    @Override
    public void run()
    {
        while (!this.timer.checkReset())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        for (Settings settings : this.settings)
        {
            settings.save();
        }

        instance = null;
    }
}