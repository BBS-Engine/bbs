package mchorse.tests;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.storage.DataBytesStorage;
import mchorse.bbs.data.storage.DataGzipStorage;
import mchorse.bbs.data.types.ByteArrayType;
import mchorse.bbs.data.types.IntArrayType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.ShortArrayType;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataStorageTest
{
    private MapType data;

    public DataStorageTest()
    {
        MapType map = new MapType();
        ListType list = new ListType();

        map.putString("a", "Hello, world!");
        map.putInt("b", 10);
        map.put("c", list);

        /* Fill the list */
        for (int i = 0; i < 1000; i++)
        {
            Vector3f vector = new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());

            list.add(DataStorageUtils.vector3fToData(vector));
        }

        /* Initialize arrays */
        byte[] bytes = new byte[1000];
        short[] shorts = new short[5000];
        int[] ints = new int[1000];

        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = (byte) (i + 512);
        }

        for (int i = 0; i < shorts.length; i++)
        {
            shorts[i] = (short) (30000 + i);
        }

        for (int i = 0; i < ints.length; i++)
        {
            ints[i] = i - 500;
        }

        map.put("d", new ByteArrayType(bytes));
        map.put("e", new ShortArrayType(shorts));
        map.put("f", new IntArrayType(ints));

        this.data = map;
    }

    @Test
    public void testCompression()
    {
        byte[] uncompressed = null;
        byte[] compressed = null;
        DataBytesStorage bytesStorage = new DataBytesStorage();
        DataGzipStorage gzipStorage = new DataGzipStorage(bytesStorage);

        try
        {
            bytesStorage.write(this.data);
            uncompressed = bytesStorage.getBytes();

            gzipStorage.write(this.data);
            compressed = bytesStorage.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Uncompressed: " + uncompressed.length + ", compressed: " + compressed.length);

        try
        {
            bytesStorage.setBytes(uncompressed);

            MapType uncompressedMap = (MapType) bytesStorage.read();

            bytesStorage.setBytes(compressed);

            MapType compressedMap = (MapType) gzipStorage.read();

            Assertions.assertEquals(uncompressedMap, this.data);
            Assertions.assertEquals(compressedMap, this.data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}