package mchorse.bbs.graphics.line;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Line <T>
{
    public List<LinePoint<T>> points = new ArrayList<LinePoint<T>>();

    public Line<T> add(float x, float y)
    {
        return this.add(x, y, null);
    }

    public Line<T> add(float x, float y, T user)
    {
        if (!this.points.isEmpty())
        {
            LinePoint<T> last = this.points.get(this.points.size() - 1);
            final float e = 0.0001F;

            if (Math.abs(last.x - x) < e && Math.abs(last.y - y) < e)
            {
                return this;
            }
        }

        this.points.add(new LinePoint<T>(x, y, user));

        return this;
    }

    public List<LinePoint<T>> build(float thickness)
    {
        List<LinePoint<T>> compiled = new ArrayList<LinePoint<T>>();

        for (int i = 0, c = points.size(); i < c; i++)
        {
            LinePoint<T> point = this.getPoint(i);
            LinePoint<T> pointPrev = this.getPoint(i - 1);
            LinePoint<T> pointNext = this.getPoint(i + 1);

            LinePoint<T> from = point;
            LinePoint<T> to = point;

            if (pointPrev != null && pointNext != null)
            {
                from = pointPrev;
                to = pointNext;
            }
            else if (pointPrev != null)
            {
                from = pointPrev;
                to = point;
            }
            else if (pointNext != null)
            {
                to = pointNext;
            }

            Vector2f perpendicular = new Vector2f(from.x, from.y).sub(to.x, to.y).perpendicular().normalize().mul(thickness);

            compiled.add(new LinePoint<T>(-perpendicular.x + point.x, -perpendicular.y + point.y, point.user));
            compiled.add(new LinePoint<T>(perpendicular.x + point.x, perpendicular.y + point.y, point.user));
        }

        return compiled;
    }

    private LinePoint<T> getPoint(int i)
    {
        if (i < 0 || i >= points.size())
        {
            return null;
        }

        return points.get(i);
    }
}