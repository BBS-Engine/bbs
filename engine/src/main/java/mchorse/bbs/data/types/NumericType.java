package mchorse.bbs.data.types;

public abstract class NumericType extends BaseType
{
    public boolean boolValue()
    {
        return this.intValue() != 0;
    }

    public byte byteValue()
    {
        return (byte) this.intValue();
    }

    public short shortValue()
    {
        return (short) this.intValue();
    }

    public abstract int intValue();

    public abstract float floatValue();

    public abstract long longValue();

    public abstract double doubleValue();
}