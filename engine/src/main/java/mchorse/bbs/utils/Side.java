package mchorse.bbs.utils;

import org.joml.Vector3i;

public enum Side
{
    TOP(0, 1, 0), BOTTOM(0, -1, 0), RIGHT(1, 0, 0), LEFT(-1, 0, 0), FRONT(0, 0, 1), BACK(0, 0, -1);

    public final Vector3i normal;

    private Side(int x, int y, int z)
    {
        this.normal = new Vector3i(x, y, z);
    }
}