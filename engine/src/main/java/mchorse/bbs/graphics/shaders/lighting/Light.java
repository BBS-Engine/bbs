package mchorse.bbs.graphics.shaders.lighting;

import mchorse.bbs.utils.colors.Color;
import org.joml.Vector3f;

public class Light
{
    public Vector3f position;
    public Color color;
    public float distance;

    public Light(Vector3f position, Color color, float distance)
    {
        this.position = position;
        this.color = color;
        this.distance = distance;
    }
}