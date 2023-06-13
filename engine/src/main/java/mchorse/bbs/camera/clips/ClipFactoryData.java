package mchorse.bbs.camera.clips;

import mchorse.bbs.camera.clips.converters.IClipConverter;
import mchorse.bbs.game.utils.factory.UIFactoryData;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.camera.clips.UIClip;

import java.util.HashMap;
import java.util.Map;

public class ClipFactoryData extends UIFactoryData<UIClip>
{
    public final Map<Link, IClipConverter<? extends Clip, ? extends Clip>> converters = new HashMap<Link, IClipConverter<? extends Clip, ? extends Clip>>();

    public ClipFactoryData(int color, Class<? extends UIClip> panelUI)
    {
        super(color, panelUI);
    }

    public ClipFactoryData withConverter(Link to, IClipConverter<? extends Clip, ? extends Clip> converter)
    {
        this.converters.put(to, converter);

        return this;
    }
}