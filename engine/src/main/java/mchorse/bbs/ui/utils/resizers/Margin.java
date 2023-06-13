package mchorse.bbs.ui.utils.resizers;

public class Margin
{
    public int left;
    public int top;
    public int right;
    public int bottom;

    public Margin all(int all)
    {
        return this.all(all, all);
    }

    public Margin all(int horizontal, int vertical)
    {
        return this.all(horizontal, vertical, horizontal, vertical);
    }

    public Margin all(int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        return this;
    }

    public Margin left(int left)
    {
        this.left = left;

        return this;
    }

    public Margin top(int top)
    {
        this.top = top;

        return this;
    }

    public Margin right(int right)
    {
        this.right = right;

        return this;
    }

    public Margin bottom(int bottom)
    {
        this.bottom = bottom;

        return this;
    }

    public int vertical()
    {
        return this.top + this.bottom;
    }

    public int horizontal()
    {
        return this.left + this.right;
    }
}