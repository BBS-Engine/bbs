package mchorse.bbs.graphics.line;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Line <T>
{
    public List<LinePoint<T>> points = new ArrayList<>();

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

        this.points.add(new LinePoint<>(x, y, user));

        return this;
    }

    public List<LinePoint<T>> build(float thickness)
    {
        List<LinePoint<T>> compiled = new ArrayList<>();

        for (int i = 0, c = points.size(); i < c; i++)
        {
            LinePoint<T> point = this.getPoint(i);
            LinePoint<T> pointPrev = this.getPoint(i - 1);
            LinePoint<T> pointNext = this.getPoint(i + 1);

            LinePoint<T> from = point;
            LinePoint<T> to = point;

            if (pointPrev != null && pointNext != null)
            {
                Vector2f perpendicularPrev = new Vector2f(pointPrev.x, pointPrev.y).sub(point.x, point.y).perpendicular().normalize().mul(thickness);
                Vector2f perpendicularNext = new Vector2f(point.x, point.y).sub(pointNext.x, pointNext.y).perpendicular().normalize().mul(thickness);

                compiled.add(new LinePoint<>(-perpendicularPrev.x + point.x, -perpendicularPrev.y + point.y, point.user));
                compiled.add(new LinePoint<>(perpendicularPrev.x + point.x, perpendicularPrev.y + point.y, point.user));
                compiled.add(new LinePoint<>(-perpendicularNext.x + point.x, -perpendicularNext.y + point.y, point.user));
                compiled.add(new LinePoint<>(perpendicularNext.x + point.x, perpendicularNext.y + point.y, point.user));

                continue;
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

            compiled.add(new LinePoint<>(-perpendicular.x + point.x, -perpendicular.y + point.y, point.user));
            compiled.add(new LinePoint<>(perpendicular.x + point.x, perpendicular.y + point.y, point.user));
        }

        return compiled;
    }

    private Vector2f getClosest(Vector2f a, Vector2f b, Vector2f ref)
    {
        return ref.distanceSquared(a) < ref.distanceSquared(b) ? a : b;
    }

    private Vector2f getFurthest(Vector2f a, Vector2f b, Vector2f ref)
    {
        return ref.distanceSquared(a) > ref.distanceSquared(b) ? a : b;
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