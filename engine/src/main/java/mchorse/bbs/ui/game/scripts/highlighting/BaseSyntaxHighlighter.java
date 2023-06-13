package mchorse.bbs.ui.game.scripts.highlighting;

import mchorse.bbs.graphics.text.FontRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseSyntaxHighlighter implements ISyntaxHighlighter
{
    protected Set<String> operators = new HashSet<String>(Arrays.asList("+", "-", "=", "/", "*", "<", ">", "~", "&", "|", "!", "?", ":"));
    protected Set<String> primaryKeywords = new HashSet<String>();
    protected Set<String> secondaryKeywords = new HashSet<String>();
    protected Set<String> identifierKeywords = new HashSet<String>();
    protected Set<String> special = new HashSet<String>();
    protected Set<String> typeKeyswords = new HashSet<String>();
    protected Pattern functionName = Pattern.compile("[\\w_][\\d\\w_]*", Pattern.CASE_INSENSITIVE);
    protected boolean identifyFunctions = true;

    protected SyntaxStyle style;

    /* Parsing runtime data */
    private String buffer;
    private char string;
    private int last;

    public BaseSyntaxHighlighter()
    {
        this.style = new SyntaxStyle();
    }

    @Override
    public SyntaxStyle getStyle()
    {
        return this.style;
    }

    @Override
    public void setStyle(SyntaxStyle style)
    {
        this.style = style == null ? this.style : style;
    }

    /**
     * Parse text segments that will be used for syntax highlighting
     */
    @Override
    public List<TextSegment> parse(FontRenderer font, List<HighlightedTextLine> textLines, String line, int lineIndex)
    {
        List<TextSegment> list = new ArrayList<TextSegment>();
        List<TextSegment> prevLine = lineIndex > 0 ? textLines.get(lineIndex - 1).segments : null;

        if (prevLine != null && !prevLine.isEmpty())
        {
            TextSegment last = prevLine.get(prevLine.size() - 1);

            if (last.color == this.style.comments && !last.text.startsWith("//") && !last.text.trim().endsWith("*/"))
            {
                list.add(new TextSegment(line, this.style.comments, 0));

                return list;
            }
        }

        this.buffer = "";
        this.string = '\0';
        this.last = 0;

        main:
        for (int i = 0, c = line.length(); i < c; i++)
        {
            char character = line.charAt(i);
            char next = i < c - 1 ? line.charAt(i + 1) : '\0';

            /* Strings */
            if (character == '\'' || character == '"')
            {
                if (this.string == '\0')
                {
                    list.add(new TextSegment(this.buffer, this.style.other, font.getWidth(this.buffer)));

                    this.buffer = "";
                    this.string = character;
                }
                else if (string == character)
                {
                    char prev = i > 0 ? line.charAt(i - 1) : '\0';

                    if (prev != '\\')
                    {
                        this.string = '\0';
                        this.buffer += character;
                        list.add(new TextSegment(this.buffer, this.style.strings, font.getWidth(this.buffer)));

                        this.buffer = "";

                        continue;
                    }
                }
            }

            boolean isString = this.string != '\0';

            /* Multiline comments */
            if (!isString && character == '/' && i < c - 1 && line.charAt(i + 1) == '*')
            {
                int lastI = i;

                i += 2;

                while (i < c)
                {
                    character = line.charAt(i);

                    if (character == '*' && i < c - 1 && line.charAt(i + 1) == '/')
                    {
                        String comment = line.substring(lastI, i + 2);

                        list.add(new TextSegment(this.buffer, this.style.other, font.getWidth(this.buffer)));
                        list.add(new TextSegment(comment, this.style.comments, font.getWidth(comment)));

                        i += 1;
                        this.buffer = "";

                        continue main;
                    }

                    i += 1;
                }

                String comment = line.substring(lastI);

                list.add(new TextSegment(this.buffer, this.style.other, font.getWidth(this.buffer)));
                list.add(new TextSegment(comment, this.style.comments, 0));

                return list;
            }

            /* One line comments */
            if (!isString && character == '/' && i < c - 1 && line.charAt(i + 1) == '/')
            {
                String comment = line.substring(i);

                list.add(new TextSegment(buffer, this.style.other, font.getWidth(this.buffer)));
                list.add(new TextSegment(comment, this.style.comments, 0));

                return list;
            }

            /* Operators */
            if (!isString && operators.contains(String.valueOf(character)))
            {
                boolean isNumericalMinus = character == '-' && Character.isDigit(next);

                /* Check for numerical minus sign, this condition above makes it possible
                 * to highlight minus sign as a part of the number literal */
                if (!isNumericalMinus)
                {
                    String sign = String.valueOf(character);

                    list.add(new TextSegment(this.buffer, this.style.other, font.getWidth(this.buffer)));
                    list.add(new TextSegment(sign, this.style.primary, font.getWidth(sign)));

                    this.buffer = "";
                    this.last = i;

                    continue;
                }
            }

            this.buffer += character;

            /* Keywords */
            if (!isString && ((next != '\0' && !this.isLegalName(next)) || i == c - 1))
            {
                if (this.last < i)
                {
                    char last = line.charAt(this.last);
                    boolean predicateForNumbers = (last == '-' || last == '.') && Character.isDigit(line.charAt(this.last + 1));

                    if (!this.isLegalName(last) && !predicateForNumbers)
                    {
                        this.last += 1;
                    }
                }

                String keyword = line.substring(this.last, i + 1);

                if (primaryKeywords.contains(keyword))
                {
                    this.pushKeyword(list, keyword, this.style.primary, i, font);
                }
                else if (special.contains(keyword))
                {
                    this.pushKeyword(list, keyword, this.style.special, i, font);
                }
                else if (secondaryKeywords.contains(keyword) || (this.identifyFunctions && this.isFunctionCall(list, keyword, next)))
                {
                    this.pushKeyword(list, keyword, this.style.secondary, i, font);
                }
                else if (identifierKeywords.contains(keyword))
                {
                    this.pushKeyword(list, keyword, this.style.identifier, i, font);
                }
                else if (this.isNumberOrConstant(keyword))
                {
                    this.pushKeyword(list, keyword, this.style.numbers, i, font);
                }
                else if (this.isIdentifier(list))
                {
                    this.pushKeyword(list, keyword, this.style.identifier, i, font);
                }
            }

            if (!this.isLegalName(character))
            {
                this.last = i;
            }
        }

        /* If there is some remaining buffer, simply push it as some ordinary text */
        if (!this.buffer.trim().isEmpty())
        {
            list.add(new TextSegment(this.buffer, this.style.other, 0));
        }

        return list;
    }

        private boolean isLegalName(char character)
    {
        return Character.isLetterOrDigit(character) || character == '_';
    }

    /**
     * Generic method to push keyword
     */
    protected void pushKeyword(List<TextSegment> list, String keyword, int color, int i, FontRenderer font)
    {
        if (this.buffer.length() > keyword.length())
        {
            String other = this.buffer.substring(0, this.buffer.length() - keyword.length());

            list.add(new TextSegment(other, this.style.other, font.getWidth(other)));
        }

        list.add(new TextSegment(keyword, color, font.getWidth(keyword)));

        this.buffer = "";
        this.last = i + 1;
    }

    /**
     * Check whether current state clarify as a function call
     */
    protected boolean isFunctionCall(List<TextSegment> list, String keyword, char next)
    {
        if (!list.isEmpty())
        {
            TextSegment previous = list.get(list.size() - 1);
            boolean bufferIsKeyword = this.buffer.trim().equals(keyword);

            if (previous.color == this.style.strings)
            {
                return false;
            }

            if (bufferIsKeyword && previous.text.equals("function"))
            {
                return false;
            }

            if (bufferIsKeyword && previous.color != this.style.other)
            {
                return false;
            }

            if (previous.text.trim().equals(keyword.trim()))
            {
                return false;
            }
        }

        Matcher matcher = functionName.matcher(keyword);
        boolean matches = matcher.matches();

        return next == '(' && matches;
    }

    /**
     * Check whether given keyword is some constant or a number literal
     */
    protected boolean isNumberOrConstant(String keyword)
    {
        if (typeKeyswords.contains(keyword))
        {
            return true;
        }

        try
        {
            Double.parseDouble(keyword);

            return true;
        }
        catch (NumberFormatException e)
        {}

        int length = keyword.trim().length();

        if (keyword.startsWith("0x") && length >= 3 && length <= 10)
        {
            try
            {
                Long.parseLong(keyword.substring(2), 16);

                return true;
            }
            catch (Exception e)
            {}
        }

        return false;
    }

    protected boolean isIdentifier(List<TextSegment> list)
    {
        if (!list.isEmpty())
        {
            TextSegment previous = list.get(list.size() - 1);

            if (previous.text.trim().equals("function") && previous.color == this.getStyle().secondary)
            {
                return true;
            }
        }

        return false;
    }
}