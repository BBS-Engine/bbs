package mchorse.bbs.graphics.text;

import mchorse.bbs.graphics.text.format.IFontFormat;
import mchorse.bbs.graphics.text.format.ResetFontFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextSplitter
{
    private List<String> lines;

    private StringBuilder builder = new StringBuilder();
    private String newFormat = "";

    private int width;
    private int index;

    public List<String> split(FontRenderer fontRenderer, String str, int width)
    {
        if (width <= 0 || str.isEmpty())
        {
            return Collections.emptyList();
        }

        Font font = fontRenderer.font;
        char prev = 0;
        int lastSpace = 0;
        int lastSpaceThreshold = 0;

        this.reset();

        for (int i = 0, c = str.length(); i < c; i++)
        {
            char character = str.charAt(i);
            Glyph glyph = font.getGlyph(character);

            if (character == ' ')
            {
                lastSpace = i;
            }
            else if (character == '\n')
            {
                this.addNewLine(str, i);
            }
            else if (character == FontRenderer.FORMATTING_CHARACTER && i < c - 1)
            {
                char formatChar = str.charAt(i + 1);
                IFontFormat format = font.formats.get((int) formatChar);

                if (format != null)
                {
                    if (format instanceof ResetFontFormat)
                    {
                        this.builder = new StringBuilder();
                    }
                    else
                    {
                        this.builder.append(format);
                    }
                }

                i += 1;
            }

            if (glyph == null)
            {
                continue;
            }

            this.width += font.getKerning(prev, character);

            if (this.width + glyph.advance + 1 > width)
            {
                if (i - lastSpace < 12 && lastSpace != lastSpaceThreshold)
                {
                    i = lastSpace;
                    lastSpaceThreshold = lastSpace;
                }

                this.addNewLine(str, i);
            }
            else
            {
                this.width += glyph.advance + 1;
                prev = character;
            }
        }

        if (this.index < str.length())
        {
            this.addNewLine(str, str.length() - 1);
        }

        return this.lines;
    }

    private void reset()
    {
        this.lines = new ArrayList<>();

        this.width = this.index = 0;
        this.builder = new StringBuilder();
        this.newFormat = "";
    }

    private void addNewLine(String str, int i)
    {
        this.lines.add(this.newFormat + str.substring(this.index, i + 1));
        this.index = i + 1;
        this.width = 0;

        this.newFormat = this.builder.toString();
    }
}