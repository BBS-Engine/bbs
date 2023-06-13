package mchorse.bbs.ui.utils;

import java.util.regex.Pattern;

public enum StringGroup
{
    SPACE("[\\s]"), ALPHANUMERIC("[\\w\\d]"), OTHER("[^\\w\\d\\s]");

    private Pattern regex;

    public static StringGroup get(String character)
    {
        for (StringGroup group : values())
        {
            if (group.match(character))
            {
                return group;
            }
        }

        return OTHER;
    }

    private StringGroup(String regex)
    {
        this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public boolean match(String character)
    {
        return this.regex.matcher(character).matches();
    }
}
