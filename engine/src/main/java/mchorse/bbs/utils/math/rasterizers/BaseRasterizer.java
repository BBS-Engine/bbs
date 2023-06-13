package mchorse.bbs.utils.math.rasterizers;

import org.joml.Vector2i;

import java.util.Set;

public abstract class BaseRasterizer
{
    public float start;
    public float end;
    public float step;

    public void setupRange(float start, float end, float step)
    {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public void solve(Set<Vector2i> points)
    {
        Vector2i prev = null;
        float i = this.start;
        float min = Math.min(this.start, this.end);
        float max = Math.max(this.start, this.end);

        while (i >= min && i <= max)
        {
            Vector2i current = this.calculate(i);

            if (current.equals(prev) || (prev != null && this.hasTwoAdjacentNeighbors(current, prev, i)))
            {
                i += this.step;

                continue;
            }

            points.add(current);

            prev = current;
            i += this.step;
        }
    }

    private boolean hasTwoAdjacentNeighbors(Vector2i current, Vector2i prev, float i)
    {
        Vector2i next = current;
        float newI = i + this.step;

        while (next.equals(current))
        {
            next = this.calculate(newI);
            newI += this.step;
        }

        int prevDiffX = Math.abs(current.x - prev.x);
        int prevDiffY = Math.abs(current.y - prev.y);
        int nextDiffX = Math.abs(current.x - next.x);
        int nextDiffY = Math.abs(current.y - next.y);

        /* There are only two cases where current has two adjacent neighbors:
         *
         * ()    |  {}[]  prevDiffX == 0 && prevDiffY == 1 &&
         * {}[]  |  ()    nextDiffX == 1 && nextDiffY == 0
         *
         *   []  |  (){}  prevDiffX == 1 && prevDiffY == 0 &&
         * (){}  |    []  nextDiffX == 0 && nextDiffY == 1
         *
         * Where () is previous vector, {} is current and [] is next.
         */
        return (prevDiffX == 0 && prevDiffY == 1 && nextDiffX == 1 && nextDiffY == 0)
            || (prevDiffX == 1 && prevDiffY == 0 && nextDiffX == 0 && nextDiffY == 1);
    }

    protected abstract Vector2i calculate(float i);
}