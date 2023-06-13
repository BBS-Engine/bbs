package mchorse.bbs.utils;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rewriter
{
    public static String rewrite(Pattern pattern, String string, Function<Matcher, String> processor)
    {
        Matcher matcher = pattern.matcher(string);
        StringBuffer result = new StringBuffer(string.length());

        while (matcher.find())
        {
            matcher.appendReplacement(result, "");
            result.append(processor.apply(matcher));
        }

        matcher.appendTail(result);

        return result.toString();
    }
}