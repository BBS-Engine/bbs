package mchorse.bbs.data.storage;

import mchorse.bbs.data.DataStorageContext;
import mchorse.bbs.data.types.BaseType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class DataStorage implements IDataStorage
{
    /**
     * Try reading a base data type from given input stream.
     */
    public static BaseType readFromStream(InputStream stream) throws IOException
    {
        DataInputStream dataInput = new DataInputStream(stream);

        char a = (char) dataInput.readByte();
        char b = (char) dataInput.readByte();
        char c = (char) dataInput.readByte();
        char d = (char) dataInput.readByte();

        String header = new String(new char[] {a, b, c, d});

        if (!header.equals("BBS1"))
        {
            throw new IllegalStateException("Given input stream has in invalid format! Header value is: " + header);
        }

        DataStorageContext context = new DataStorageContext(dataInput);

        context.read();

        BaseType type = BaseType.fromData(context);

        stream.close();

        return type;
    }

    /**
     * Write given base type to an output stream.
     */
    public static void writeToStream(OutputStream stream, BaseType type) throws IOException
    {
        DataOutputStream dataOutput = new DataOutputStream(stream);
        DataStorageContext context = new DataStorageContext(dataOutput);

        dataOutput.writeByte((int) 'B');
        dataOutput.writeByte((int) 'B');
        dataOutput.writeByte((int) 'S');
        dataOutput.writeByte((int) '1');

        type.traverseKeys(context);
        context.write();

        BaseType.toData(context, type);

        stream.close();
    }

    @Override
    public BaseType read() throws IOException
    {
        InputStream inputStream = this.getInputStream();
        BaseType type = null;

        try
        {
            type = readFromStream(inputStream);
        }
        finally
        {
            inputStream.close();
        }

        return type;
    }

    protected abstract InputStream getInputStream() throws IOException;

    @Override
    public void write(BaseType type) throws IOException
    {
        try (OutputStream outputStream = this.getOutputStream())
        {
            writeToStream(outputStream, type);
        }
    }

    protected abstract OutputStream getOutputStream() throws IOException;
}