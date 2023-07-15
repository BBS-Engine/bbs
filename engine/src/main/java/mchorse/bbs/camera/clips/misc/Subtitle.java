package mchorse.bbs.camera.clips.misc;

public class Subtitle
{
    public String label = "";
    public int x;
    public int y;
    public float size;
    public float anchorX;
    public float anchorY;
    public float windowX;
    public float windowY;
    public int color;

    public void update(String label, int x, int y, float size, float anchorX, float anchorY, int color)
    {
        this.label = label;
        this.x = x;
        this.y = y;
        this.size = size;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.color = color;
    }

    public void updateWindow(float x, float y)
    {
        this.windowX = x;
        this.windowY = y;
    }
}