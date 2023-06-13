package mchorse.bbs.ui.game.scripts.highlighting;

import mchorse.bbs.graphics.text.FontRenderer;

import java.util.List;

public interface ISyntaxHighlighter
{
    public SyntaxStyle getStyle();

    public void setStyle(SyntaxStyle style);

    public List<TextSegment> parse(FontRenderer font, List<HighlightedTextLine> textLines, String line, int lineIndex);
}