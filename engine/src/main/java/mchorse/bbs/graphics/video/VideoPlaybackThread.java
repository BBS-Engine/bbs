package mchorse.bbs.graphics.video;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;

public class VideoPlaybackThread implements Runnable
{
    private FFmpegFrameGrabber grabber;
    private Link texture;

    private boolean stop;
    private double frameRate;

    public VideoPlaybackThread(FFmpegFrameGrabber grabber, Link texture)
    {
        this.grabber = grabber;
        this.texture = texture;

        this.frameRate = this.grabber.getFrameRate();

        new Thread(this, "Video playback " + texture).start();
    }

    public void stop()
    {
        this.stop = true;
    }

    private Texture getCurrentTexture()
    {
        return BBS.getTextures().createTexture(this.texture);
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (this.stop)
            {
                try
                {
                    this.grabber.stop();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                break;
            }

            long time = System.currentTimeMillis();
            Frame frame = null;

            do
            {
                try
                {
                    frame = this.grabber.grab();
                }
                catch (FFmpegFrameGrabber.Exception e)
                {
                    e.printStackTrace();
                }
            }
            while (frame != null && (frame.image == null || frame.image.length == 0));

            if (frame == null)
            {
                try
                {
                    this.grabber.setVideoTimestamp(0);
                }
                catch (FFmpegFrameGrabber.Exception e)
                {
                    throw new RuntimeException(e);
                }

                continue;
            }

            final Frame finalFrame = frame;

            BBS.getRender().postRunnable(() ->
            {
                if (finalFrame.image == null || finalFrame.image.length == 0)
                {
                    return;
                }

                Texture texture = this.getCurrentTexture();

                try
                {
                    texture.bind();
                    texture.width = finalFrame.imageWidth;
                    texture.height = finalFrame.imageHeight;

                    ByteBuffer pixels = (ByteBuffer) finalFrame.image[0];

                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, finalFrame.imageWidth, finalFrame.imageHeight, 0, GL12.GL_BGR, GL11.GL_UNSIGNED_BYTE, pixels);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            });

            try
            {
                long sleep = (long) (1000L / this.frameRate) - (System.currentTimeMillis() - time);

                if (sleep > 0)
                {
                    Thread.sleep(sleep);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}