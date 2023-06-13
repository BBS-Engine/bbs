package mchorse.bbs.utils.resources;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.texture.Texture;

import java.util.Stack;

public class MultiLinkThread implements Runnable
{
    private static MultiLinkThread instance;
    private static Thread thread;

    public Stack<MultiLink> links = new Stack<MultiLink>();

    public static synchronized void add(MultiLink location)
    {
        if (instance != null && !thread.isAlive())
        {
            instance = null;
        }

        if (instance == null)
        {
            instance = new MultiLinkThread();
            instance.addLink(location);
            thread = new Thread(instance);
            thread.start();
        }
        else
        {
            instance.addLink(location);
        }
    }

    public static void clear()
    {
        instance = null;
    }

    public synchronized void addLink(MultiLink link)
    {
        if (this.links.contains(link))
        {
            return;
        }

        this.links.add(link);
    }

    @Override
    public void run()
    {
        while (!this.links.isEmpty() && instance != null)
        {
            MultiLink location = this.links.peek();
            Texture texture = BBS.getTextures().textures.get(location);

            try
            {
                if (texture != null)
                {
                    this.links.pop();

                    Pixels pixels = TextureProcessor.process(location);

                    BBS.getEngine().scheduledRunnables.add(() ->
                    {
                        texture.bind();
                        texture.uploadTexture(pixels);

                        if (texture.isMipmap())
                        {
                            texture.generateMipmap();
                        }
                    });
                }

                Thread.sleep(100);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        instance = null;
        thread = null;
    }
}