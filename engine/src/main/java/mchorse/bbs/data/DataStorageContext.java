package mchorse.bbs.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataStorageContext
{
    public final DataInputStream in;
    public final DataOutputStream out;

    private Map<String, Integer> keyMap;
    private Map<Integer, String> intMap;
    private int index;
    private KeyType type = KeyType.BYTE;

    public DataStorageContext(DataInputStream in)
    {
        this.in = in;
        this.out = null;
    }

    public DataStorageContext(DataOutputStream out)
    {
        this.in = null;
        this.out = out;
    }

    public String getKey(int index)
    {
        return this.intMap == null ? null : this.intMap.get(index);
    }

    public int getIndex(String key)
    {
        return this.keyMap == null ? -1 : this.keyMap.get(key);
    }

    public void put(String key)
    {
        if (this.keyMap == null)
        {
            this.keyMap = new HashMap<String, Integer>();
        }

        if (!this.keyMap.containsKey(key))
        {
            this.keyMap.put(key, this.index);
            this.index += 1;
        }
    }

    public void read() throws IOException
    {
        this.intMap = new HashMap<Integer, String>();
        this.type = KeyType.from(this.in.readByte());

        int c = this.type.read(this.in);

        for (int i = 0; i < c; i++)
        {
            this.intMap.put(this.type.read(this.in), this.in.readUTF());
        }
    }

    public String readKey() throws IOException
    {
        return this.getKey(this.type.read(this.in));
    }

    public void write() throws IOException
    {
        if (this.keyMap == null)
        {
            this.keyMap = new HashMap<String, Integer>();
        }

        if (this.keyMap.size() <= 256)
        {
            this.type = KeyType.BYTE;
        }
        else if (this.keyMap.size() <= 65536)
        {
            this.type = KeyType.SHORT;
        }
        else
        {
            this.type = KeyType.INT;
        }

        this.out.writeByte(this.type.type);
        this.type.write(this.out, this.keyMap.size());

        for (Map.Entry<String, Integer> entry : this.keyMap.entrySet())
        {
            this.type.write(this.out, entry.getValue());
            this.out.writeUTF(entry.getKey());
        }
    }

    public void writeIndex(String key) throws IOException
    {
        this.type.write(this.out, this.getIndex(key));
    }
}