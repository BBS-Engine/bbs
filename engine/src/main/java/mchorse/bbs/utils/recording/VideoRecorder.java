package mchorse.bbs.utils.recording;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.core.Engine;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.ui.utils.UIUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideoRecorder
{
    public File movies;
    public Engine engine;

    private Process process;
    private WritableByteChannel channel;
    private boolean recording;

    private ByteBuffer buffer;
    private Texture texture;

    public VideoRecorder(File movies, Engine engine)
    {
        this.movies = movies;
        this.movies.mkdirs();

        this.engine = engine;
    }

    public boolean isRecording()
    {
        return this.recording;
    }

    /**
     * Start recording the video using ffmpeg
     */
    public void startRecording(Texture texture)
    {
        if (this.recording)
        {
            return;
        }

        this.texture = texture;

        int width = texture.width;
        int height = texture.height;

        if (this.buffer == null)
        {
            this.buffer = MemoryUtil.memAlloc(width * height * 3);
        }

        this.engine.toggleRealTime(false);

        try
        {
            Path path = Paths.get(this.movies.toString());
            String movieName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String params = BBSSettings.videoEncoderArguments.get();

            params = params.replace("%WIDTH%", String.valueOf(width));
            params = params.replace("%HEIGHT%", String.valueOf(height));
            params = params.replace("%FPS%", String.valueOf(this.engine.frameRate));
            params = params.replace("%NAME%", movieName);

            List<String> args = new ArrayList<String>();

            args.add(BBSSettings.videoEncoderPath.get());
            args.addAll(Arrays.asList(params.split(" ")));

            ProcessBuilder builder = new ProcessBuilder(args);

            builder.directory(path.toFile());
            builder.redirectErrorStream(true);
            builder.redirectOutput(path.resolve(movieName.concat(".log")).toFile());

            this.process = builder.start();

            OutputStream os = this.process.getOutputStream();

            this.channel = Channels.newChannel(os);
            this.recording = true;

            UIUtils.playClick(2F);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Stop recording
     */
    public void stopRecording()
    {
        if (!this.recording)
        {
            return;
        }

        this.texture = null;

        if (this.buffer != null)
        {
            MemoryUtil.memFree(this.buffer);

            this.buffer = null;
        }

        try
        {
            if (this.channel.isOpen())
            {
                this.channel.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            this.process.waitFor(1, TimeUnit.MINUTES);
            this.process.destroy();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.engine.toggleRealTime(true);
        this.recording = false;

        UIUtils.playClick(0.5F);
    }

    /**
     * Record a frame
     */
    public void recordFrame()
    {
        if (!this.recording)
        {
            return;
        }

        this.buffer.rewind();
        this.texture.bind();
        GL11.glGetTexImage(this.texture.target, 0, GL12.GL_BGR, GL11.GL_UNSIGNED_BYTE, this.buffer);
        this.buffer.rewind();

        try
        {
            this.channel.write(this.buffer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.engine.nextFrame();
    }

    /**
     * Toggle recording of the video
     */
    public void toggleRecording(Texture texture)
    {
        if (this.recording)
        {
            this.stopRecording();
        }
        else
        {
            this.startRecording(this.texture);
        }

        UIUtils.playClick();
    }
}