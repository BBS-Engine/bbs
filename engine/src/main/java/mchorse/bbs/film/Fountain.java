package mchorse.bbs.film;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Fountain
{
    public static final Pattern CHARACTER = Pattern.compile("^[\\p{Lu} _-]+$");

    public static List<Reply> parseReplies(String screenplay)
    {
        List<Reply> replies = new ArrayList<>();
        String name = null;
        String chapter = "";
        List<String> text = new ArrayList<>();

        for (String line : screenplay.split("\n"))
        {
            if (line.startsWith("#"))
            {
                chapter = line.substring(line.lastIndexOf('#') + 1).trim();
            }
            else if (name != null)
            {
                if (line.trim().isEmpty() && !text.isEmpty())
                {
                    replies.add(new Reply(name, String.join("\n", text), chapter));
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
            replies.add(new Reply(name, String.join("\n", text), chapter));
        }

        return replies;
    }

    public static class Reply
    {
        public String name;
        public String reply;
        public String chapter;

        public Reply(String name, String reply, String chapter)
        {
            this.name = name;
            this.reply = reply;
            this.chapter = chapter;
        }
    }
}