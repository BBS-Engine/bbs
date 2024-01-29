package mchorse.bbs.camera.clips.misc;

import mchorse.bbs.utils.pose.Transform;

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
    public int backgroundColor;
    public float backgroundOffset;
    public float shadow;
    public boolean shadowOpaque;

    public Transform transform;
    public float factor;

    public int lineHeight;
    public int maxWidth;

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

    public void updateBackground(int backgroundColor, float backgroundOffset, float shadow, boolean shadowOpaque)
    {
        this.backgroundColor = backgroundColor;
        this.backgroundOffset = backgroundOffset;
        this.shadow = shadow;
        this.shadowOpaque = shadowOpaque;
    }

    public void updateTransform(Transform transform, float factor)
    {
        this.transform = transform;
        this.factor = factor;
    }

    public void updateConstraints(int lineHeight, int maxWidth)
    {
        this.lineHeight = lineHeight;
        this.maxWidth = maxWidth;
    }
}