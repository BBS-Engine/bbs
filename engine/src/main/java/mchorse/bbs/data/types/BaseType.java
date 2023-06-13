package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;

public abstract class BaseType
{
    public static final byte TYPE_MAP = 0;
    public static final byte TYPE_LIST = 1;
    public static final byte TYPE_STRING = 2;
    public static final byte TYPE_BYTE = 3;
    public static final byte TYPE_SHORT = 4;
    public static final byte TYPE_INT = 5;
    public static final byte TYPE_FLOAT = 6;
    public static final byte TYPE_LONG = 7;
    public static final byte TYPE_DOUBLE = 8;
    public static final byte TYPE_BYTE_ARRAY = 9;
    public static final byte TYPE_SHORT_ARRAY = 10;
    public static final byte TYPE_INT_ARRAY = 11;

    public static BaseType fromData(DataStorageContext context) throws IOException
    {
        byte type = context.in.readByte();
        BaseType output = null;

        if (type == TYPE_MAP) output = new MapType();
        else if (type == TYPE_LIST) output = new ListType();
        else if (type == TYPE_STRING) output = new StringType();
        else if (type == TYPE_BYTE) output = new ByteType();
        else if (type == TYPE_SHORT) output = new ShortType();
        else if (type == TYPE_INT) output = new IntType();
        else if (type == TYPE_FLOAT) output = new FloatType();
        else if (type == TYPE_LONG) output = new LongType();
        else if (type == TYPE_DOUBLE) output = new DoubleType();
        else if (type == TYPE_BYTE_ARRAY) output = new ByteArrayType();
        else if (type == TYPE_SHORT_ARRAY) output = new ShortArrayType();
        else if (type == TYPE_INT_ARRAY) output = new IntArrayType();

        if (output != null)
        {
            output.read(context);

            return output;
        }

        throw new IllegalStateException("Data type " + type + " doesn't exist!");
    }

    public static void toData(DataStorageContext context, BaseType type) throws IOException
    {
        context.out.writeByte(type.getTypeId());
        type.write(context);
    }

    public static boolean isMap(BaseType data)
    {
        return is(data, TYPE_MAP);
    }

    public static boolean isList(BaseType data)
    {
        return is(data, TYPE_LIST);
    }

    public static boolean isString(BaseType data)
    {
        return is(data, TYPE_STRING);
    }

    public static boolean isNumeric(BaseType data)
    {
        return data instanceof NumericType;
    }

    public static boolean isPrimitive(BaseType data)
    {
        return isString(data) || isNumeric(data);
    }

    public static boolean is(BaseType data, byte type)
    {
        return data != null && data.getTypeId() == type;
    }

    public void traverseKeys(DataStorageContext context)
    {}

    public boolean isMap()
    {
        return this instanceof MapType;
    }

    public boolean isList()
    {
        return this instanceof ListType;
    }

    public boolean isString()
    {
        return this instanceof StringType;
    }

    public boolean isNumeric()
    {
        return this instanceof NumericType;
    }

    public MapType asMap()
    {
        return (MapType) this;
    }

    public ListType asList()
    {
        return (ListType) this;
    }

    public String asString()
    {
        return ((StringType) this).value;
    }

    public NumericType asNumeric()
    {
        return (NumericType) this;
    }

    public abstract byte getTypeId();

    public abstract BaseType copy();

    public abstract void read(DataStorageContext context) throws IOException;

    public abstract void write(DataStorageContext context) throws IOException;
}