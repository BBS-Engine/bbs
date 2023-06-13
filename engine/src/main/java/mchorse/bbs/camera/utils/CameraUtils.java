package mchorse.bbs.camera.utils;

public class CameraUtils
{
    public static float parseAspectRation(String ratio, float old)
    {
        try
        {
            return Float.parseFloat(ratio);
        }
        catch (Exception e)
        {
            try
            {
                String[] strips = ratio.split(":");

                if (strips.length >= 2)
                {
                    return Float.parseFloat(strips[0]) / Float.parseFloat(strips[1]);
                }
            }
            catch (Exception ee)
            {}
        }

        return old;
    }
}