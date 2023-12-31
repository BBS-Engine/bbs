package mchorse.bbs.graphics.texture;

import mchorse.bbs.BBS;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.utils.resources.MultiLink;
import mchorse.bbs.utils.resources.Pixels;
import mchorse.bbs.utils.watchdog.IWatchDogListener;
import mchorse.bbs.utils.watchdog.WatchDogEvent;
import org.lwjgl.opengl.GL11;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TextureManager implements IDisposable, IWatchDogListener
{
    public final Map<Link, Texture> textures = new HashMap<>();
    public AssetProvider provider;

    private Texture error;
    private TextureExtruder extruder = new TextureExtruder();

    public TextureManager(AssetProvider provider)
    {
        this.provider = provider;
    }

    public TextureExtruder getExtruder()
    {
        return this.extruder;
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

    public Pixels getPixels(Link link) throws Exception
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

        return pixels;
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
                Pixels pixels = this.getPixels(link);

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

    public void reload()
    {
        this.reload(false);
    }

    public void reload(boolean delete)
    {
        Iterator<Texture> it = this.textures.values().iterator();

        while (it.hasNext())
        {
            Texture texture = it.next();

            if (texture.isRefreshable() || delete)
            {
                texture.delete();

                it.remove();
            }
        }

        this.textures.clear();
        this.extruder.deleteAll();
    }

    @Override
    public void delete()
    {
        this.reload(true);
    }

    /**
     * Watch dog listener implementation. This method should reload any texture
     * from "assets" source (which is in game's assets folder).
     */
    @Override
    public void accept(Path path, WatchDogEvent event)
    {
        Link link = BBS.getProvider().getLink(path.toFile());

        if (link == null)
        {
            return;
        }

        Texture texture = this.textures.remove(link);

        if (texture != null)
        {
            texture.delete();
        }

        this.extruder.delete(link);
    }
}