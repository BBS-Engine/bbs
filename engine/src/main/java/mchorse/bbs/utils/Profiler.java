package mchorse.bbs.utils;

import java.util.ArrayList;
import java.util.List;

public class Profiler
{
    private String key;

    private long last = -1;
    private List<Timestamp> timestamps = new ArrayList<>();

    public void reset()
    {
        this.key = null;

        this.last = -1;
        this.timestamps.clear();
    }

    public void begin(String key)
    {
        this.key = key;

        if (this.last < 0)
        {
            this.last = System.currentTimeMillis();
        }
    }

    public void endBegin(String key)
    {
        this.end();
        this.begin(key);
    }

    public void end()
    {
        long time = System.currentTimeMillis();

        this.timestamps.add(new Timestamp(this.key, time - this.last));

        this.last = time;
        this.key = null;
    }

    public void print()
    {
        System.out.println("Profiler result:");
        System.out.println();
        long timeSum = 0L;

        for (Timestamp timestamp : this.timestamps)
        {
            System.out.println("- '" + timestamp.key + "' took " + timestamp.toSeconds() + " seconds");

            timeSum += timestamp.time;
        }

        System.out.println();
        System.out.println("In total passed " + (timeSum / 1000F) + " seconds");
    }

    public static class Timestamp
    {
        public String key;
        public long time;

        public Timestamp(String key, long time)
        {
            this.key = key;
            this.time = time;
        }

        public float toSeconds()
        {
            return this.time / 1000F;
        }
    }
}