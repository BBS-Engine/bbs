package mchorse.bbs.audio;

import mchorse.bbs.audio.ogg.VorbisReader;
import mchorse.bbs.audio.wav.WaveReader;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;

public class AudioReader
{
    public static Wave read(AssetProvider provider, Link link) throws Exception
    {
        if (link.path.endsWith(".wav"))
        {
            return new WaveReader().read(provider.getAsset(link));
        }
        else if (link.path.endsWith(".ogg"))
        {
            return VorbisReader.read(link, provider.getAsset(link));
        }

        throw new IllegalStateException("Given link " + link.toString() + " isn't a Wave or a Vorbis file!");
    }
}