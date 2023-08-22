package mchorse.bbs.utils.keyframes;

import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.math.Interpolations;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class KeyframeSimplifier
{
    private static final Vector2d[] directions = new Vector2d[] {
        new Vector2d(1, 0),
        new Vector2d(-1, 0),
        new Vector2d(0, 1),
        new Vector2d(0, -1),
    };

    /**
     * Simple simplify. It doesn't try to adjust the curve to fit among keyframes,
     * but rather just removes all the keyframes between a minimum and a maximum.
     */
    public static KeyframeChannel simpleSimplify(KeyframeChannel channel)
    {
        KeyframeChannel newChannel = new KeyframeChannel();
        List<List<Keyframe>> segments = getSegments(channel);

        for (List<Keyframe> keyframes : segments)
        {
            Keyframe first = keyframes.get(0);
            Keyframe last = keyframes.get(keyframes.size() - 1);

            newChannel.insert(first.tick, first.value);
            newChannel.insert(last.tick, last.value);
        }

        return newChannel;
    }

    /**
     * Simplify that fits bezier control points between minimums and maximumus.
     */
    public static KeyframeChannel simplify(KeyframeChannel channel)
    {
        KeyframeChannel newChannel = new KeyframeChannel();
        List<List<Keyframe>> segments = getSegments(channel);

        for (List<Keyframe> keyframes : segments)
        {
            Pair<Keyframe, Keyframe> pair = fitKeyframes(keyframes, 100);

            int a = newChannel.insert(pair.a.tick, pair.a.value);
            int b = newChannel.insert(pair.b.tick, pair.b.value);
            Keyframe left = newChannel.get(a);
            Keyframe right = newChannel.get(b);

            left.interp = right.interp = KeyframeInterpolation.BEZIER;
            left.rx = pair.a.rx;
            left.ry = pair.a.ry;
            right.lx = pair.b.lx;
            right.ly = pair.b.ly;
        }

        return newChannel;
    }

    /**
     * Tries to brute forcefully calculate left and right control points for given
     * list of points.
     */
    private static Pair<Keyframe, Keyframe> fitKeyframes(List<Keyframe> keyframes, int iterations)
    {
        KeyframeChannel channel = new KeyframeChannel();
        Keyframe left = new Keyframe();
        Keyframe right = new Keyframe();

        channel.keyframes.addAll(keyframes);
        channel.sort();

        left.copy(keyframes.get(0));
        right.copy(keyframes.get(keyframes.size() - 1));
        left.interp = right.interp = KeyframeInterpolation.BEZIER;
        left.rx = 5;
        left.ry = 0;
        right.lx = 5;
        right.ly = 0;

        double score = Double.MAX_VALUE;
        double speed = 1D;

        Keyframe leftTmp = new Keyframe();
        Keyframe rightTmp = new Keyframe();

        /* Brute force find the control point that fit the keyframes */
        for (; iterations > 0; iterations--)
        {
            leftTmp.copy(left);
            rightTmp.copy(right);

            double newScore = score;
            float rx = left.rx;
            float ry = left.ry;
            float lx = rightTmp.lx;
            float ly = rightTmp.ly;

            for (Vector2d d1 : directions)
            {
                for (Vector2d d2 : directions)
                {
                    leftTmp.rx = (float) (left.rx + d1.x * speed);
                    leftTmp.ry = (float) (left.ry + d1.y * speed);
                    rightTmp.lx = (float) (right.lx + d2.x * speed);
                    rightTmp.ly = (float) (right.ly + d2.y * speed);

                    double currentScore = getScore(channel, leftTmp, rightTmp);

                    if (currentScore < newScore)
                    {
                        newScore = currentScore;
                        rx = leftTmp.rx;
                        ry = leftTmp.ry;
                        lx = rightTmp.lx;
                        ly = rightTmp.ly;
                    }
                }
            }

            if (newScore < score)
            {
                left.rx = rx;
                left.ry = ry;
                right.lx = lx;
                right.ly = ly;
                score = newScore;
            }
            else
            {
                speed /= 2D;
            }

            /* If it's close enough, there is no point to keep pushing it */
            if (score < 0.001)
            {
                break;
            }
        }

        return new Pair<>(left, right);
    }

    /**
     * Get score between a channel of linear keyframes and two bezier handles.
     *
     * It's used to determine whether control points are moving in the correct
     * direction.
     */
    private static double getScore(KeyframeChannel channel, Keyframe left, Keyframe right)
    {
        double score = 0;

        for (int i = 0; i < 10; i++)
        {
            float tick = Interpolations.lerp(left.tick, right.tick, i / 10F);
            double value = left.interpolate(right, (tick - left.tick) / (double) (right.tick - left.tick));
            double channelValue = channel.interpolate(tick);

            score += Math.abs(channelValue - value);
        }

        return score;
    }

    /**
     * Returns a list of keyframes that are either incremented or decremented from
     * the keyframe channel. For example imagine this graph:
     *
     * <pre><code>
     *            X
     *        O__/ \       X
     *       /      \O_   O \
     *      /          \ /   O
     *     X            X     X
     * </code></pre>
     *
     * X's and O's are keyframes. This method will return segments between X's including
     * both X's. So in turn, you'll get from this graph:
     *
     * [0, 1, 2], [2, 3, 4], [4, 5, 6], and [6, 7, 8].
     *
     * The point is to break down a keyframe channel into simpler parts for which it
     * will be possible to find control points.
     */
    private static List<List<Keyframe>> getSegments(KeyframeChannel channel)
    {
        List<List<Keyframe>> segments = new ArrayList<>();
        List<Keyframe> segment = new ArrayList<>();
        double direction = 0;

        for (Keyframe point : channel.getKeyframes())
        {
            if (!segment.isEmpty())
            {
                Keyframe lastPoint = segment.get(segment.size() - 1);
                double newDirection = Math.copySign(1D, lastPoint.value - point.value);

                if (Math.abs(direction - newDirection) > 1)
                {
                    segments.add(segment);

                    segment = new ArrayList<>();
                    segment.add(lastPoint);
                    segment.add(point);

                    direction = 0;
                }
                else
                {
                    segment.add(point);
                    direction = newDirection;
                }
            }
            else
            {
                segment.add(point);
            }
        }

        if (segment.size() > 1)
        {
            segments.add(segment);
        }

        return segments;
    }
}