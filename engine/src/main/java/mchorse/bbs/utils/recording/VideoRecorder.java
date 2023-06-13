package mchorse.bbs.utils.recording;

import mchorse.bbs.core.Engine;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.ui.utils.UIUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoRecorder
{
    public File movies;
    public Engine engine;

    private boolean recording;

    private FFmpegFrameRecorder recorder;
    private FFmpegFrameFilter filter;

    private ByteBuffer buffer;
    private Framebuffer framebuffer;

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
    public void startRecording(Framebuffer framebuffer)
    {
        if (this.recording)
        {
            return;
        }

        this.framebuffer = framebuffer;

        Texture texture = framebuffer.getMainTexture();

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

            this.recorder = new FFmpegFrameRecorder(path.resolve(movieName + ".mp4").toFile(), 0);

            this.recorder.setFrameRate(this.engine.frameRate);
            this.recorder.setImageWidth(width);
            this.recorder.setImageHeight(height);
            this.recorder.setVideoQuality(2);
            this.recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);

            this.filter = new FFmpegFrameFilter("vflip", width, height);

            this.recorder.start();
            this.filter.start();

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

        this.framebuffer = null;

        if (this.buffer != null)
        {
            MemoryUtil.memFree(this.buffer);

            this.buffer = null;
        }

        try
        {
            this.recorder.stop();
            this.filter.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.recorder = null;
        this.filter = null;

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

        Texture mainTexture = this.framebuffer.getMainTexture();

        this.buffer.rewind();
        mainTexture.bind();
        GL11.glGetTexImage(mainTexture.target, 0, GL12.GL_BGR, GL11.GL_UNSIGNED_BYTE, this.buffer);
        this.buffer.rewind();

        try
        {
            Frame frame = new Frame();

            int width = mainTexture.width;
            int height = mainTexture.height;

            frame.imageWidth = width;
            frame.imageHeight = height;
            frame.imageDepth = Frame.DEPTH_BYTE;
            frame.imageChannels = 3;
            frame.image = new Buffer[]{this.buffer};
            frame.imageStride = ((width * frame.imageChannels * Frame.pixelSize(frame.imageDepth) + 7) & ~7) / Frame.pixelSize(frame.imageDepth);
            frame.type = Frame.Type.VIDEO;

            this.filter.push(frame, avutil.AV_PIX_FMT_BGR24);
            this.recorder.record(this.filter.pull(), avutil.AV_PIX_FMT_BGR24);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            this.stopRecording();
        }

        this.engine.nextFrame();
    }

    /**
     * Toggle recording of the video
     */
    public void toggleRecording(Framebuffer framebuffer)
    {
        if (this.recording)
        {
            this.stopRecording();
        }
        else
        {
            this.startRecording(framebuffer);
        }

        UIUtils.playClick();
    }
}