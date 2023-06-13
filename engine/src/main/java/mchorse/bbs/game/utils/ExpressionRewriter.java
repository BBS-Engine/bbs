package mchorse.bbs.game.utils;

import mchorse.bbs.BBSData;
import mchorse.bbs.utils.Rewriter;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionRewriter
{
    public Pattern pattern;
    public DataContext context;
    public DecimalFormat formater;

    public ExpressionRewriter()
    {
        this.pattern = Pattern.compile("\\$\\{([^\\}]+)\\}");
        this.formater = new DecimalFormat("0.######");
    }

    public ExpressionRewriter set(DataContext context)
    {
        this.context = context;

        return this;
    }

    public String replace(String string)
    {
        return Rewriter.rewrite(this.pattern, string, this::processor);
    }

    private String processor(Matcher matcher)
    {
        String string = matcher.group(1);
        Object value = "";

        try
        {
            if (!string.isEmpty())
            {
                value = BBSData.getScripts().repl(string).output;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (value instanceof Number)
        {
            return this.formater.format(((Number) value).doubleValue());
        }
        else if (value instanceof String)
        {
            return (String) value;
        }

        return "";
    }
}