package mchorse.bbs.camera.clips;

import mchorse.bbs.camera.clips.converters.IClipConverter;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.factory.UIFactoryData;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.utils.icons.Icon;

import java.util.HashMap;
import java.util.Map;

public class ClipFactoryData extends UIFactoryData<UIClip>
{
    public final Map<Link, IClipConverter<? extends Clip, ? extends Clip>> converters = new HashMap<>();
    public final Icon icon;

    public ClipFactoryData(Icon icon, int color, Class<? extends UIClip> panelUI)
    {
        super(color, panelUI);

        this.icon = icon;
    }

    public ClipFactoryData withConverter(Link to, IClipConverter<? extends Clip, ? extends Clip> converter)
    {
        this.converters.put(to, converter);

        return this;
    }
}