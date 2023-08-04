package mchorse.bbs.graphics.text;

import mchorse.bbs.graphics.text.format.IFontFormat;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FontRendererContext
{
    public final Color color = Color.white();
    public float time;

    public int index;
    public float x;
    public float y;
    public int skew;

    public final Set<IFontFormat> activeFormats = new HashSet<IFontFormat>();
    public final Random random = new Random();

    public void reset()
    {
        for (IFontFormat format : this.activeFormats)
        {
            format.reset();
        }

        this.color.set(Colors.WHITE);
        this.skew = 0;
        this.activeFormats.clear();
    }

    public void setup(int index, int x, int y)
    {
        this.index = index;
        this.x = x;
        this.y = y;

        this.random.setSeed((long) ((index + this.time) * 100000F));
    }
}