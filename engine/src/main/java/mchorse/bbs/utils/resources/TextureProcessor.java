package mchorse.bbs.utils.resources;

import mchorse.bbs.BBS;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class TextureProcessor
{
    public static Pixels process(MultiLink multi)
    {
        List<Pixels> images = new ArrayList<Pixels>();

        int w = 0;
        int h = 0;

        for (int i = 0; i < multi.children.size(); i++)
        {
            FilteredLink child = multi.children.get(i);

            try
            {
                Pixels pixels = Pixels.fromPNGStream(BBS.getProvider().getAsset(child.path));

                w = Math.max(w, child.getWidth(pixels.width));
                h = Math.max(h, child.getHeight(pixels.height));

                images.add(pixels);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        Pixels output = Pixels.fromSize(w, h);

        for (int i = 0; i < multi.children.size(); i++)
        {
            Pixels pixels = images.get(i);
            FilteredLink filter = multi.children.get(i);
            int iw = pixels.width;
            int ih = pixels.height;

            if (filter.scaleToLargest)
            {
                iw = w;
                ih = h;
            }
            else if (filter.scale != 0 && filter.scale > 0)
            {
                iw = (int) (iw * filter.scale);
                ih = (int) (ih * filter.scale);
            }

            if (iw > 0 && ih > 0)
            {
                if (filter.erase)
                {
                    processErase(output, pixels, filter, iw, ih);
                }
                else
                {
                    if (filter.color != Colors.WHITE || filter.pixelate > 1)
                    {
                        processImage(pixels, filter);
                    }

                    output.draw(pixels, filter.shiftX, filter.shiftY, iw, ih);
                }
            }

            pixels.delete();
        }

        output.rewindBuffer();

        return output;
    }

    /**
     * Apply erasing
     */
    private static void processErase(Pixels image, Pixels pixels, FilteredLink filter, int iw, int ih)
    {
        Pixels mask = Pixels.fromSize(image.width, image.height);

        mask.draw(pixels, filter.shiftX, filter.shiftY, iw, ih);

        for (int p = 0, c = mask.getCount(); p < c; p++)
        {
            Color pixel = mask.getColor(p);

            if (pixel.a > 0.999F)
            {
                pixel = image.getColor(p);
                pixel.a = 0;
                image.setColor(p, pixel);
            }
        }

        mask.delete();
    }

    /**
     * Apply filters
     */
    private static void processImage(Pixels pixels, FilteredLink link)
    {
        Color filter = new Color().set(link.color);
        Color pixel = new Color();
        int pixelate = link.pixelate;

        for (int i = 0, c = pixels.getCount(); i < c; i++)
        {
            pixel.copy(pixels.getColor(i));

            if (pixelate > 1)
            {
                int x = pixels.toX(i);
                int y = pixels.toY(i);
                boolean origin = x % pixelate == 0 && y % pixelate == 0;

                x -= x % pixelate;
                y -= y % pixelate;

                pixel.copy(pixels.getColor(x, y));
                pixels.setColor(i, pixel);

                if (!origin)
                {
                    continue;
                }
            }

            pixel.r *= filter.r;
            pixel.g *= filter.g;
            pixel.b *= filter.b;
            pixel.a *= filter.a;
            pixels.setColor(i, pixel);
        }
    }
}