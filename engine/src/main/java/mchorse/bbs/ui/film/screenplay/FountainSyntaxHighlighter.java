package mchorse.bbs.ui.film.screenplay;

import mchorse.bbs.film.Film;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.HighlightedTextLine;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.ISyntaxHighlighter;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.SyntaxStyle;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.TextSegment;

import java.util.ArrayList;
import java.util.List;

public class FountainSyntaxHighlighter implements ISyntaxHighlighter
{
    private SyntaxStyle style;

    public FountainSyntaxHighlighter()
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
        this.style = style;
    }

    @Override
    public List<TextSegment> parse(FontRenderer font, List<HighlightedTextLine> textLines, String line, int lineIndex)
    {
        List<TextSegment> list = new ArrayList<>();

        if (Film.CHARACTER.matcher(line).matches())
        {
            list.add(new TextSegment(line, this.style.primary, font.getWidth(line)));
        }
        else if (line.trim().startsWith("INT") || line.trim().startsWith("EXT"))
        {
            list.add(new TextSegment(line, this.style.secondary, font.getWidth(line)));
        }
        else if (Film.METADATA.matcher(line).matches())
        {
            list.add(new TextSegment(line, this.style.numbers, font.getWidth(line)));
        }
        else if (line.trim().startsWith("#"))
        {
            list.add(new TextSegment(line, this.style.identifier, font.getWidth(line)));
        }
        else
        {
            list.add(new TextSegment(line, this.style.other, font.getWidth(line)));
        }

        return list;
    }
}