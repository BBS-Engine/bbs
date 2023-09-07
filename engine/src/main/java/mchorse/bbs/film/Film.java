package mchorse.bbs.film;

import mchorse.bbs.BBS;
import mchorse.bbs.film.tts.ScreenplayReply;
import mchorse.bbs.film.replays.Replays;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.utils.clips.Clips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Film extends ValueGroup
{
    public static final Pattern CHARACTER = Pattern.compile("^[\\p{Lu} _-]+$");
    public static final Pattern METADATA = Pattern.compile("^[\\w\\d_-]+\\:.*$");

    public final Clips camera = new Clips("camera", BBS.getFactoryCameraClips());
    public final Replays replays = new Replays("replays");
    public final ValueString screenplay = new ValueString("screenplay", "");

    public Film()
    {
        super("");

        this.add(this.camera);
        this.add(this.replays);
        this.add(this.screenplay);
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