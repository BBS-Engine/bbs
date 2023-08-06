package mchorse.bbs.cubic.data.animation;

import java.util.ArrayList;
import java.util.List;

public class AnimationChannel
{
    public List<AnimationVector> keyframes = new ArrayList<>();

    public void sort()
    {
        this.keyframes.sort((a, b) ->
        {
            double diff = a.time - b.time;

            return diff < 0 ? -1 : (diff >= 0 ? 1 : 0);
        });

        AnimationVector previous = null;

        for (AnimationVector vector : this.keyframes)
        {
            if (previous != null)
            {
                previous.next = vector;
                vector.prev = previous;
            }

            previous = vector;
        }
    }
}