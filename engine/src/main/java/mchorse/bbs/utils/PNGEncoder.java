package mchorse.bbs.utils;

import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.resources.Pixels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * Stupid simple PNGEncoder, because awt hangs macOS. :(
 *
 * @link http://www.libpng.org/pub/png/spec/1.2/PNG-Contents.html
 * @link https://www.nayuki.io/res/dumb-png-output-java/DumbPngOutput.java
 */
public class PNGEncoder
{
    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    public static void writeToFile(Pixels pixels, File file) throws IOException
    {
        PNGEncoder encoder = new PNGEncoder();
        byte[] bytes = encoder.encode(pixels);

        FileOutputStream stream = new FileOutputStream(file);

        stream.write(bytes);
        stream.close();
    }

    public byte[] encode(Pixels pixels) throws IOException
    {
        this.writeSignature();
        this.writeIHDRChunk(pixels);
        this.writeIDATChunk(pixels);
        this.writeIENDChunk();

        return this.bytes.toByteArray();
    }

    /* Chunks */

    private void writeSignature()
    {
        /* http://www.libpng.org/pub/png/spec/1.2/PNG-Structure.html#PNG-file-signature */
        this.bytes.write(137);
        this.bytes.write(80);
        this.bytes.write(78);
        this.bytes.write(71);
        this.bytes.write(13);
        this.bytes.write(10);
        this.bytes.write(26);
        this.bytes.write(10);
    }

    private void writeIHDRChunk(Pixels pixels) throws IOException
    {
        ByteArrayOutputStream IHDR = new ByteArrayOutputStream();

        /* IHDR data chunk length (it's constant) */
        this.writeInt(this.bytes, 13);

        /* IHDR chunk ID */
        this.writeString(IHDR, "IHDR");

        /* Width and height */
        this.writeInt(IHDR, pixels.width);
        this.writeInt(IHDR, pixels.height);
        /* Bit depth (8 = 0..255 per sample) */
        IHDR.write(8);
        /* Color type (6 = RGBA) */
        IHDR.write(6);
        /* Compression type (0 = default compression) */
        IHDR.write(0);
        /* Filter method (0 = default filter method) */
        IHDR.write(0);
        /* Interlace method (0 = no interlacing) */
        IHDR.write(0);

        this.writeChunk(IHDR);
    }

    private void writeIDATChunk(Pixels pixels) throws IOException
    {
        ByteArrayOutputStream IDAT = new ByteArrayOutputStream();
        ByteArrayOutputStream scanlines = new ByteArrayOutputStream();

        for (int y = 0; y < pixels.height; y++)
        {
            scanlines.write(0);

            for (int x = 0; x < pixels.width; x++)
            {
                Color color = pixels.getColor(x, y);

                scanlines.write((int) (color.r * 255F));
                scanlines.write((int) (color.g * 255F));
                scanlines.write((int) (color.b * 255F));
                scanlines.write((int) (color.a * 255F));
            }
        }

        Deflater deflater = new Deflater(8);
        ByteArrayOutputStream finalOutput = new ByteArrayOutputStream();
        DeflaterOutputStream stream = new DeflaterOutputStream(finalOutput, deflater);

        byte[] original = scanlines.toByteArray();
        stream.write(original);
        stream.finish();
        stream.flush();

        byte[] zipped = finalOutput.toByteArray();

        /* Length, header, compressed bytes, and CRC */
        this.writeInt(this.bytes, zipped.length);
        this.writeString(IDAT, "IDAT");
        IDAT.write(zipped);

        this.writeChunk(IDAT);
    }

    private void writeIENDChunk() throws IOException
    {
        ByteArrayOutputStream IEND = new ByteArrayOutputStream();

        /* Length, Header, (no data), and CRC */
        this.writeInt(this.bytes, 0);
        this.writeString(IEND, "IEND");

        this.writeChunk(IEND);
    }

    /* Helpers */

    private void writeString(ByteArrayOutputStream stream, String string)
    {
        for (int i = 0; i < string.length(); i++)
        {
            stream.write(string.charAt(i));
        }
    }

    private void writeInt(ByteArrayOutputStream stream, int integer)
    {
        byte b1 = (byte) ((integer >> 24) & 0xff);
        byte b2 = (byte) ((integer >> 16) & 0xff);
        byte b3 = (byte) ((integer >> 8) & 0xff);
        byte b4 = (byte) ((integer >> 0) & 0xff);

        stream.write(b1);
        stream.write(b2);
        stream.write(b3);
        stream.write(b4);
    }

    private void writeChunk(ByteArrayOutputStream stream) throws IOException
    {
        byte[] bytes = stream.toByteArray();

        this.bytes.write(bytes);
        this.writeCRC(bytes);
    }

    private void writeCRC(byte[] bytes)
    {
        CRC32 crc32 = new CRC32();

        crc32.update(bytes.length == 0 ? new byte[] {0} : bytes);

        long crc = crc32.getValue();
        int b1 = (int) ((crc >> 24) & 0xff);
        int b2 = (int) ((crc >> 16) & 0xff);
        int b3 = (int) ((crc >> 8) & 0xff);
        int b4 = (int) ((crc >> 0) & 0xff);

        this.bytes.write(b1);
        this.bytes.write(b2);
        this.bytes.write(b3);
        this.bytes.write(b4);
    }
}