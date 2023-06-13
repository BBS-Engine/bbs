package mchorse.bbs.audio.wav;

import mchorse.bbs.audio.BinaryChunk;
import mchorse.bbs.audio.BinaryReader;
import mchorse.bbs.audio.Wave;

import java.io.InputStream;

/**
 * @link http://soundfile.sapp.org/doc/WaveFormat/
 */
public class WaveReader extends BinaryReader
{
    public Wave read(InputStream stream) throws Exception
    {
        try
        {
            BinaryChunk main = this.readChunk(stream);

            if (!main.id.equals("RIFF"))
            {
                throw new Exception("Given file is not 'RIFF'! It's '" + main.id + "' instead...");
            }

            String format = this.readFourString(stream);

            if (!format.equals("WAVE"))
            {
                throw new Exception("Given RIFF file is not a 'WAVE' file! It's '" + format + "' instead...");
            }

            int audioFormat = -1;
            int numChannels = -1;
            int sampleRate = -1;
            int byteRate = -1;
            int blockAlign = -1;
            int bitsPerSample = -1;
            byte[] data = null;

            int read = 0;

            while (read < 2)
            {
                BinaryChunk chunk = this.readChunk(stream);

                if (chunk.id.equals("fmt "))
                {
                    audioFormat = this.readShort(stream);
                    numChannels = this.readShort(stream);

                    sampleRate = this.readInt(stream);
                    byteRate = this.readInt(stream);

                    blockAlign = this.readShort(stream);
                    bitsPerSample = this.readShort(stream);

                    /* Discarding extra data */
                    if (chunk.size > 16)
                    {
                        stream.skip(chunk.size - 16);
                    }

                    read++;
                }
                else if (chunk.id.equals("data"))
                {
                    data = new byte[chunk.size];
                    stream.read(data);
                    read++;
                }
                else
                {
                    this.skip(stream, chunk.size);
                }
            }

            stream.close();

            return new Wave(audioFormat, numChannels, sampleRate, byteRate, blockAlign, bitsPerSample, data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public BinaryChunk readChunk(InputStream stream) throws Exception
    {
        String id = this.readFourString(stream);
        int size = this.readInt(stream);

        return new BinaryChunk(id, size);
    }
}