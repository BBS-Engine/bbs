package mchorse.bbs.cubic;

import mchorse.bbs.cubic.data.animation.Animations;
import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.parsing.AnimationParser;
import mchorse.bbs.cubic.parsing.ModelParser;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class CubicLoader
{
    public LoadingInfo load(MolangParser parser, InputStream stream, String path)
    {
        LoadingInfo info = new LoadingInfo();

        try
        {
            MapType root = this.loadFile(stream);

            if (root.has("model"))
            {
                info.model = ModelParser.parse(parser, root.getMap("model"));
            }

            if (root.has("animations"))
            {
                MapType animations = root.getMap("animations");

                info.animations = new Animations();

                for (String key : animations.keys())
                {
                    info.animations.add(AnimationParser.parse(parser, key, animations.getMap(key)));
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("An error happened when parsing BBS model file: " + path);
            e.printStackTrace();
        }

        return info;
    }

    private MapType loadFile(InputStream stream)
    {
        try
        {
            return DataToString.mapFromString(this.loadStringFile(stream));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String loadStringFile(InputStream stream) throws IOException
    {
        String content = IOUtils.readText(stream);

        stream.close();

        return content;
    }

    public static class LoadingInfo
    {
        public Animations animations;
        public Model model;
    }
}