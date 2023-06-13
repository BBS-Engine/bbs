package mchorse.bbs.graphics.texture;

import mchorse.bbs.BBS;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.graphics.video.VideoPlaybackThread;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.utils.resources.MultiLink;
import mchorse.bbs.utils.resources.Pixels;
import mchorse.bbs.utils.watchdog.IWatchDogListener;
import mchorse.bbs.utils.watchdog.WatchDogEvent;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TextureManager implements IDisposable, IWatchDogListener
{
    public final Map<Link, Texture> textures = new HashMap<Link, Texture>();
    public final Map<Link, VideoPlaybackThread> videos = new HashMap<Link, VideoPlaybackThread>();
    public AssetProvider provider;

    private Texture error;

    public TextureManager(AssetProvider provider)
    {
        this.provider = provider;
    }

    private Texture getError()
    {
        if (this.error == null)
        {
            try
            {
                this.error = this.getTexture(Link.assets("textures/error.png"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return this.error;
    }

    public void bind(Link texture)
    {
        this.getTexture(texture).bind();
    }

    public void bind(Link texture, int unit)
    {
        this.getTexture(texture).bind(unit);
    }

    public boolean has(Link link)
    {
        return this.getTexture(link) != this.getError();
    }

    public void delete(Link link)
    {
        Texture texture = this.textures.remove(link);

        if (texture != null)
        {
            texture.delete();
        }
    }

    public Texture createTexture(Link link)
    {
        return this.createTexture(link, GL11.GL_NEAREST);
    }

    public Texture createTexture(Link link, int filter)
    {
        Texture texture = this.textures.get(link);

        if (texture == null || texture == this.getError())
        {
            texture = new Texture();
            texture.setFilter(filter);

            this.textures.put(link, texture);
        }

        return texture;
    }

    public Texture getTexture(Link link)
    {
        return this.getTexture(link, GL11.GL_NEAREST);
    }

    public Texture getTexture(Link link, int filter)
    {
        Texture texture = this.textures.get(link);

        if (texture == null)
        {
            try
            {
                Pixels pixels;

                if (link instanceof MultiLink)
                {
                    pixels = LinkUtils.getStreamForMultiLink((MultiLink) link);
                }
                else
                {
                    pixels = Pixels.fromPNGStream(this.provider.getAsset(link));
                }

                if (pixels != null)
                {
                    texture = new Texture();
                    texture.setFilter(filter);
                    texture.uploadTexture(pixels);
                    texture.unbind();

                    System.out.println("Texture \"" + link + "\" was loaded!");

                    this.textures.put(link, texture);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();

                texture = this.getError();

                this.textures.put(link, texture);
            }
        }

        return texture;
    }

    /* Video playback */

    public boolean playVideo(Link link)
    {
        File file = BBS.getProvider().getFile(link);

        if (file != null)
        {
            try
            {
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);

                grabber.start();

                VideoPlaybackThread video = new VideoPlaybackThread(grabber, link);

                this.videos.put(link, video);

                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean stopVideo(Link link)
    {
        VideoPlaybackThread video = this.videos.remove(link);

        if (video != null)
        {
            video.stop();
        }

        return video != null;
    }

    @Override
    public void delete()
    {
        for (VideoPlaybackThread video : this.videos.values())
        {
            video.stop();
        }

        this.videos.clear();

        for (Texture texture : this.textures.values())
        {
            texture.delete();
        }

        this.textures.clear();
    }

    /**
     * Watch dog listener implementation. This method should reload any texture
     * from "assets" source (which is in game's assets folder).
     */
    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        Link link = IWatchDogListener.getAssetsLink(path);

        Texture texture = this.textures.remove(link);

        if (texture != null)
        {
            texture.delete();
        }
    }
}