package mchorse.bbs.ui.framework.elements.input.text.utils;

import mchorse.bbs.utils.math.MathUtils;

public class Cursor
{
    public int line;
    public int offset;

    public Cursor()
    {}

    public Cursor(int line, int offset)
    {
        this.set(line, offset);
    }

    public void set(int line, int offset)
    {
        this.line = line;
        this.offset = offset;
    }

    public void copy(Cursor cursor)
    {
        this.set(cursor.line, cursor.offset);
    }

    /* String clipping */

    public String start(String line)
    {
        return this.start(line, 0);
    }

    public String start(String line, int offset)
    {
        return line.isEmpty() ? line : line.substring(0, this.getOffset(line, offset));
    }

    public String end(String line)
    {
        return this.end(line, 0);
    }

    public String end(String line, int offset)
    {
        return line.isEmpty() ? line : line.substring(this.getOffset(line, offset));
    }

    public int getOffset(String line)
    {
        return this.getOffset(line, 0);
    }

    public int getOffset(String line, int offset)
    {
        return MathUtils.clamp(this.offset + offset, 0, line.length());
    }

    public boolean isEmpty()
    {
        return this.line < 0;
    }

    /**
     * Whether this cursor is less to given cursor
     */
    public boolean isThisLessTo(Cursor cursor)
    {
        if (this.line == cursor.line)
        {
            return this.offset < cursor.offset;
        }

        return this.line < cursor.line;
    }

    /**
     * Whether this cursor is less or equals to given cursor
     */
    public boolean isThisLessOrEqualTo(Cursor cursor)
    {
        if (this.line == cursor.line)
        {
            return this.offset <= cursor.offset;
        }

        return this.line < cursor.line;
    }

    /**
     * Whether this cursor is equal to given cursor
     */
    public boolean isEqualTo(Cursor cursor)
    {
        return this.line == cursor.line && this.offset == cursor.offset;
    }
}