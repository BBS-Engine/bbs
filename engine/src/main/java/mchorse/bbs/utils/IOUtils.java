package mchorse.bbs.utils;

import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IOUtils
{
    public static String readText(File file) throws FileNotFoundException
    {
        return readText(new FileInputStream(file));
    }

    /**
     * Read a text file from current jar's resources
     */
    public static String readText(String path)
    {
        try
        {
            InputStream in = IOUtils.class.getResourceAsStream(path);

            return readText(in);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to read file: " + path);
        }
    }

    /**
     * Read a text file from {@link InputStream} 
     */
    public static String readText(InputStream in)
    {
        Scanner scanner = new Scanner(new InputStreamReader(in, StandardCharsets.UTF_8));
        String result = scanner.useDelimiter("\\A").next();

        scanner.close();

        return result;
    }

    public static void writeText(File file, String string) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));

        writer.write(string);
        writer.close();
    }

    /**
     * Read all lines from a file (needs a text file) 
     */
    public static List<String> readLines(String fileName) throws Exception
    {
        return readLines(IOUtils.class.getClass().getResourceAsStream(fileName));
    }

    /**
     * Read all lines from a file (needs a text file) 
     */
    public static List<String> readLines(InputStream stream) throws Exception
    {
        List<String> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
        {
            String line;

            while ((line = br.readLine()) != null)
            {
                list.add(line);
            }
        }

        return list;
    }

    /**
     * <b>IMPORTANT</b>: don't forget to free the memory using {@link MemoryUtil#memFree(Buffer)}
     * after using the byte buffer!
     */
    public static ByteBuffer readByteBuffer(InputStream stream, int bufferSize) throws IOException
    {
        byte[] bytes = IOUtils.readBytes(stream, bufferSize);
        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);

        buffer.put(bytes);
        buffer.flip();

        return buffer;
    }

    public static byte[] readBytes(InputStream stream) throws IOException
    {
        return readBytes(stream, 4 * 1024);
    }

    public static byte[] readBytes(InputStream stream, int bufferSize) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int bytesRead;
        byte[] data = new byte[bufferSize];

        while ((bytesRead = stream.read(data, 0, data.length)) != -1)
        {
            buffer.write(data, 0, bytesRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    public static void deleteFolder(File folder)
    {
        if (!folder.isDirectory())
        {
            return;
        }

        for (File file : folder.listFiles())
        {
            if (file.isDirectory())
            {
                deleteFolder(file);
            }
            else
            {
                file.delete();
            }
        }

        folder.delete();
    }

    public static File findNonExistingFile(File file)
    {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        String baseName = name.substring(0, index);
        String extension = name.substring(index);

        int i = 1;

        while (file.exists())
        {
            file = new File(file.getParentFile().getAbsolutePath(), baseName + "_" + i + extension);

            i += 1;
        }

        return file;
    }
}