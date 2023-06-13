package mchorse.bbs.ui.utils;

import mchorse.bbs.ui.framework.elements.input.text.utils.Cursor;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.math.MathUtils;

public class StringGroupMatcher
{
    private StringGroup lastGroup;

    /**
     * Find a group (two cursors) at given cursor
     */
    public Pair<Integer, Integer> findGroup(int direction, String line, int offset)
    {
        if (line.isEmpty())
        {
            return null;
        }

        int first = direction < 0 && offset > 0 ? offset - 1 : offset;

        first = MathUtils.clamp(first, 0, line.length() - 1);

        String character = String.valueOf(line.charAt(first));
        StringGroup group = StringGroup.get(character);

        int min = offset;
        int max = offset;

        this.lastGroup = null;

        if (direction <= 0)
        {
            while (min > 0)
            {
                if (this.matchSelectGroup(group, String.valueOf(line.charAt(min - 1))))
                {
                    min -= 1;
                }
                else
                {
                    break;
                }
            }
        }

        this.lastGroup = null;

        if (direction >= 0)
        {
            while (max < line.length())
            {
                if (this.matchSelectGroup(group, String.valueOf(line.charAt(max))))
                {
                    max += 1;
                }
                else
                {
                    break;
                }
            }
        }

        return new Pair<Integer, Integer>(min, max);
    }

    private boolean matchSelectGroup(StringGroup group, String character)
    {
        if (group.match(character))
        {
            return this.lastGroup == null;
        }

        if (group == StringGroup.SPACE)
        {
            if (this.lastGroup == null)
            {
                this.lastGroup = StringGroup.get(character);
            }

            return StringGroup.get(character) == this.lastGroup;
        }

        return false;
    }
}