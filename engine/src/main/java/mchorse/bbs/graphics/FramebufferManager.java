package mchorse.bbs.graphics;

import mchorse.bbs.core.IDisposable;
import mchorse.bbs.resources.Link;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FramebufferManager implements IDisposable
{
    public final Map<Link, Framebuffer> framebuffers = new HashMap<Link, Framebuffer>();

    public Framebuffer getFramebuffer(Link key, Consumer<Framebuffer> setup)
    {
        Framebuffer framebuffer = this.framebuffers.get(key);

        if (framebuffer == null)
        {
            framebuffer = new Framebuffer();

            setup.accept(framebuffer);

            this.framebuffers.put(key, framebuffer);
        }

        return framebuffer;
    }

    @Override
    public void delete()
    {
        for (Framebuffer framebuffer : this.framebuffers.values())
        {
            framebuffer.delete();
        }

        this.framebuffers.clear();
    }
}