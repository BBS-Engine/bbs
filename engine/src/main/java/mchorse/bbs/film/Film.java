package mchorse.bbs.film;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.film.values.ValueReplays;
import mchorse.bbs.film.tts.ScreenplayReply;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.utils.clips.values.ValueClips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Film extends StructureBase
{
    public static final Pattern CHARACTER = Pattern.compile("^[\\p{Lu} _-]+$");
    public static final Pattern METADATA = Pattern.compile("^[\\w\\d_-]+\\:.*$");

    public final ValueInt length = new ValueInt("length", 0);
    public final ValueClips camera = new ValueClips("camera", BBS.getFactoryClips());
    public final ValueReplays replays = new ValueReplays("replays");
    public final ValueString screenplay = new ValueString("screenplay", "");

    public Film()
    {
        this.register(this.length);
        this.register(this.camera);
        this.register(this.replays);
        this.register(this.screenplay);
    }

    public List<ScreenplayReply> parseReplies()
    {
        List<ScreenplayReply> replies = new ArrayList<>();
        String name = null;
        String chapter = "";
        List<String> text = new ArrayList<>();

        for (String line : this.screenplay.get().split("\n"))
        {
            if (line.startsWith("#"))
            {
                chapter = line.substring(line.lastIndexOf('#') + 1).trim();
            }
            else if (name != null)
            {
                if (line.trim().isEmpty() && !text.isEmpty())
                {
                    replies.add(new ScreenplayReply(name, String.join("\n", text), chapter));
                    text.clear();

                    name = null;
                }
                else
                {
                    text.add(line);
                }
            }
            else if (CHARACTER.matcher(line).matches())
            {
                name = line;
            }
        }

        if (name != null && !text.isEmpty())
        {
            replies.add(new ScreenplayReply(name, String.join("\n", text), chapter));
        }

        return replies;
    }

    public Map<String, String> parseMetadata()
    {
        Map<String, String> metadata = new HashMap<>();

        for (String line : this.screenplay.get().split("\n"))
        {
            int delimiter = line.indexOf(':');

            if (delimiter >= 0)
            {
                String key = line.substring(0, delimiter).trim();
                String value = line.substring(delimiter + 1).trim();

                metadata.put(key, value);
            }
            else
            {
                break;
            }
        }

        return metadata;
    }
}