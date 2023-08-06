package mchorse.bbs.data.types;

import mchorse.bbs.data.DataStorageContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

public class ListType extends BaseType implements Iterable<BaseType>
{
    public final List<BaseType> elements = new ArrayList<>();

    /* Accessors */

    public boolean isEmpty()
    {
        return this.elements.isEmpty();
    }

    public int size()
    {
        return this.elements.size();
    }

    public BaseType get(int index)
    {
        return this.has(index) ? this.elements.get(index) : null;
    }

    public void add(BaseType type)
    {
        this.elements.add(type);
    }

    public void add(int index, BaseType type)
    {
        this.elements.add(index, type);
    }

    public void remove(int index)
    {
        this.elements.remove(index);
    }

    public boolean has(int index)
    {
        return this.has(index, -1);
    }

    public boolean has(int index, int type)
    {
        boolean has = index >= 0 && index < this.elements.size();

        if (type >= 0)
        {
            has = has && this.elements.get(index).getTypeId() == type;
        }

        return has;
    }

    /* Byte array accessors */

    public void addByteArray(byte[] value)
    {
        this.add(new ByteArrayType(value));
    }

    public void addByte(int index, byte[] value)
    {
        this.add(index, new ByteArrayType(value));
    }

    public byte[] getByteArray(int index)
    {
        return this.getByteArray(index, ByteArrayType.DEFAULT);
    }

    public byte[] getByteArray(int index, byte[] defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.is(value, BaseType.TYPE_BYTE_ARRAY) ? ((ByteArrayType) value).value : defaultValue;
    }

    /* Boolean accessors */

    public void addBool(boolean value)
    {
        this.add(new ByteType(value));
    }

    public void addBool(int index, boolean value)
    {
        this.add(index, new ByteType(value));
    }

    public boolean getBool(int index)
    {
        return this.getBool(index, false);
    }

    public boolean getBool(int index, boolean defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isNumeric(value) ? value.asNumeric().boolValue() : defaultValue;
    }

    /* Byte accessors */

    public void addByte(byte value)
    {
        this.add(new ByteType(value));
    }

    public void addByte(int index, byte value)
    {
        this.add(index, new ByteType(value));
    }

    public byte getByte(int index)
    {
        return this.getByte(index, (byte) 0);
    }

    public byte getByte(int index, byte defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isNumeric(value) ? value.asNumeric().byteValue() : defaultValue;
    }

    /* Short accessors */

    public void addShort(short value)
    {
        this.add(new ShortType(value));
    }

    public void addShort(int index, short value)
    {
        this.add(index, new ShortType(value));
    }

    public short getShort(int index)
    {
        return this.getShort(index, (short) 0);
    }

    public short getShort(int index, short defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isNumeric(value) ? value.asNumeric().shortValue() : defaultValue;
    }

    /* Int accessors */

    public void addInt(int value)
    {
        this.add(new IntType(value));
    }

    public void addInt(int index, int value)
    {
        this.add(index, new IntType(value));
    }

    public int getInt(int index)
    {
        return this.getInt(index, 0);
    }

    public int getInt(int index, int defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isNumeric(value) ? value.asNumeric().intValue() : defaultValue;
    }

    /* Float accessors */

    public void addFloat(float value)
    {
        this.add(new FloatType(value));
    }

    public void addFloat(int index, float value)
    {
        this.add(index, new FloatType(value));
    }

    public float getFloat(int index)
    {
        return this.getFloat(index, (float) 0);
    }

    public float getFloat(int index, float defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isNumeric(value) ? value.asNumeric().floatValue() : defaultValue;
    }

    /* Long accessors */

    public void addLong(long value)
    {
        this.add(new LongType(value));
    }

    public void addLong(int index, long value)
    {
        this.add(index, new LongType(value));
    }

    public long getLong(int index)
    {
        return this.getLong(index, (long) 0);
    }

    public long getLong(int index, long defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isNumeric(value) ? value.asNumeric().longValue() : defaultValue;
    }

    /* Double accessors */

    public void addDouble(double value)
    {
        this.add(new DoubleType(value));
    }

    public void addDouble(int index, double value)
    {
        this.add(index, new DoubleType(value));
    }

    public double getDouble(int index)
    {
        return this.getDouble(index, (double) 0);
    }

    public double getDouble(int index, double defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isNumeric(value) ? value.asNumeric().doubleValue() : defaultValue;
    }

    /* String accessors */

    public void addString(String value)
    {
        this.add(new StringType(value));
    }

    public void addString(int index, String value)
    {
        this.add(index, new StringType(value));
    }

    public String getString(int index)
    {
        return this.getString(index, "");
    }

    public String getString(int index, String defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isString(value) ? ((StringType) value).value : defaultValue;
    }

    /* Map and list accessors */

    public ListType getList(int index)
    {
        return this.getList(index, new ListType());
    }

    public ListType getList(int index, ListType defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isList(value) ? (ListType) value : defaultValue;
    }

    public MapType getMap(int index)
    {
        return this.getMap(index, new MapType());
    }

    public MapType getMap(int index, MapType defaultValue)
    {
        BaseType value = this.get(index);

        return BaseType.isMap(value) ? (MapType) value : defaultValue;
    }

    /* Implementations */

    @Override
    public void traverseKeys(DataStorageContext context)
    {
        super.traverseKeys(context);

        for (BaseType type : this.elements)
        {
            type.traverseKeys(context);
        }
    }

    @Override
    public byte getTypeId()
    {
        return BaseType.TYPE_LIST;
    }

    @Override
    public BaseType copy()
    {
        ListType list = new ListType();

        for (BaseType type : this)
        {
            list.add(type.copy());
        }

        return list;
    }

    @Override
    public void read(DataStorageContext context) throws IOException
    {
        this.elements.clear();

        for (int i = 0, count = context.in.readInt(); i < count; i++)
        {
            BaseType type = BaseType.fromData(context);

            this.elements.add(type);
        }
    }

    @Override
    public void write(DataStorageContext context) throws IOException
    {
        context.out.writeInt(this.elements.size());

        for (BaseType type : this.elements)
        {
            BaseType.toData(context, type);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ListType)
        {
            return this.elements.equals(((ListType) obj).elements);
        }

        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(",");

        for (BaseType data : this)
        {
            joiner.add(data.toString());
        }

        return "[" + joiner.toString() + "]";
    }

    @Override
    public Iterator<BaseType> iterator()
    {
        return this.elements.iterator();
    }
}