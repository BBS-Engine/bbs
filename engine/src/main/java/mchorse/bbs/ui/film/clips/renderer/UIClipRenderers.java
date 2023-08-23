package mchorse.bbs.ui.film.clips.renderer;

import mchorse.bbs.BBS;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.camera.clips.misc.AudioClip;

import java.util.HashMap;
import java.util.Map;

public class UIClipRenderers
{
    private UIClipRenderer defaultRenderer;

    private Map<Class, IUIClipRenderer> renderers = new HashMap<>();

    public UIClipRenderers()
    {
        this.defaultRenderer = new UIClipRenderer();

        this.register(AudioClip.class, new UIAudioClipRenderer());

        BBS.events.post(new RegisterUIClipRenderers(this));
    }

    public void register(Class key, IUIClipRenderer renderer)
    {
        this.renderers.put(key, renderer);
    }

    public <T extends Clip> IUIClipRenderer<T> get(T clip)
    {
        IUIClipRenderer renderer = this.renderers.get(clip.getClass());

        return renderer == null ? this.defaultRenderer : renderer;
    }
}