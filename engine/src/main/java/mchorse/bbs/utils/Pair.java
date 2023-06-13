package mchorse.bbs.utils;

import java.util.Objects;

public class Pair <A, B>
{
    public A a;
    public B b;

    public Pair(A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof Pair)
        {
            Pair pair = (Pair) obj;

            return Objects.equals(this.a, pair.b) && Objects.equals(this.b, pair.b);
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.a, this.b);
    }
}