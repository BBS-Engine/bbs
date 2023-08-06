package mchorse.bbs.ui.framework.elements.input.text;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.text.undo.TextEditUndo;
import mchorse.bbs.ui.framework.elements.input.text.utils.Cursor;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.HighlightedTextLine;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.ISyntaxHighlighter;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.JSSyntaxHighlighter;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.SyntaxStyle;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.TextLineNumber;
import mchorse.bbs.ui.framework.elements.input.text.highlighting.TextSegment;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UITextEditor extends UITextarea<HighlightedTextLine>
{
    private ISyntaxHighlighter highlighter;
    private int placements;
    private boolean lines = true;

    private List<TextLineNumber> numbers = new ArrayList<>(40);
    private int lineNumber = 0;

    public UITextEditor(Consumer<String> callback)
    {
        super(callback);

        this.highlighter = new JSSyntaxHighlighter();
    }

    @Override
    protected FontRenderer getFont()
    {
        return BBS.getFonts().getRenderer(Link.assets("fonts/bbs_round_mono.json"));
    }

    public UITextEditor highlighter(ISyntaxHighlighter highlighter)
    {
        this.highlighter = highlighter;

        return this;
    }

    @Override
    protected HighlightedTextLine createTextLine(String line)
    {
        return new HighlightedTextLine(line);
    }

    public UITextEditor noLineNumbers()
    {
        this.lines = false;

        return this;
    }

    public ISyntaxHighlighter getHighlighter()
    {
        return this.highlighter;
    }

    public void resetHighlight()
    {
        for (HighlightedTextLine textLine : this.text)
        {
            textLine.resetSegments();
        }
    }

    public void updateHighlighter()
    {
        SyntaxStyle style = new SyntaxStyle();

        if (this.highlighter.getStyle() != style)
        {
            this.highlighter.setStyle(style);
            this.resetHighlight();
        }
    }

    @Override
    public void setText(String text)
    {
        super.setText(text);

        /* It will be null before when it will get called from parent's constructor */
        this.resetHighlight();
    }

    @Override
    protected void recalculateSizes()
    {
        /* Calculate how many pixels will number lines will occupy horizontally */
        double power = Math.ceil(Math.log10(this.text.size() + 1));

        this.placements = (int) power * 6;

        super.recalculateSizes();
    }

    @Override
    protected void changedLine(int i)
    {
        String line = this.text.get(i).text;

        if (line.contains("/*") || line.contains("*/"))
        {
            this.changedLineAfter(i);
        }
        else
        {
            super.changedLine(i);
            this.text.get(i).resetSegments();
        }
    }

    @Override
    protected void changedLineAfter(int i)
    {
        super.changedLineAfter(i);

        while (i < this.text.size())
        {
            this.text.get(i).resetSegments();

            i += 1;
        }
    }

    /* Change input behavior */

    @Override
    protected String getFromChar(char typedChar)
    {
        if (
            this.wasDoubleInsert(typedChar, ')', '(') ||
            this.wasDoubleInsert(typedChar, ']', '[') ||
            this.wasDoubleInsert(typedChar, '}', '{') ||
            this.wasDoubleInsert(typedChar, '"', '"') ||
            this.wasDoubleInsert(typedChar, '\'', '\'')
        ) {
            this.moveCursor(1, 0);
            this.playSound("insert");

            return "";
        }

        if (typedChar == '(')
        {
            return "()";
        }
        else if (typedChar == '[')
        {
            return "[]";
        }
        else if (typedChar == '{')
        {
            return "{}";
        }
        else if (typedChar == '"')
        {
            return "\"\"";
        }
        else if (typedChar == '\'')
        {
            return "''";
        }

        return super.getFromChar(typedChar);
    }

    private boolean wasDoubleInsert(char input, char target, char supplementary)
    {
        if (input != target)
        {
            return false;
        }

        String line = this.text.get(this.cursor.line).text;

        return line.length() >= 2
            && this.cursor.offset > 0
            && this.cursor.offset < line.length()
            && line.charAt(this.cursor.offset) == target
            && line.charAt(this.cursor.offset - 1) == supplementary;
    }

    @Override
    protected void keyNewLine(TextEditUndo undo)
    {
        String line = this.text.get(this.cursor.line).text;
        boolean unwrap = line.length() >= 2
            && this.cursor.offset > 0
            && this.cursor.offset < line.length()
            && line.charAt(this.cursor.offset) == '}'
            && line.charAt(this.cursor.offset - 1) == '{';

        int indent = this.getIndent(line) + (unwrap ? 4 : 0);

        super.keyNewLine(undo);

        String margin = this.createIndent(indent);

        this.writeString(margin);
        this.cursor.offset = indent;

        undo.postText += margin;

        if (unwrap)
        {
            super.keyNewLine(undo);

            margin = this.createIndent(indent - 4);

            this.writeString(margin);
            this.cursor.line -= 1;
            this.cursor.offset = indent;

            undo.postText += margin;
        }
    }

    @Override
    protected void keyBackspace(TextEditUndo undo, boolean ctrl)
    {
        String line = this.text.get(this.cursor.line).text;

        line = this.cursor.start(line);

        if (!line.isEmpty() && line.trim().isEmpty())
        {
            int offset = 4 - line.length() % 4;

            this.startSelecting();
            this.cursor.offset -= offset;

            String deleted = this.getSelectedText();

            this.deleteSelection();
            this.deselect();

            undo.text = deleted;
        }
        else
        {
            super.keyBackspace(undo, ctrl);
        }
    }

    @Override
    protected void keyTab(boolean shift, TextEditUndo undo)
    {
        if (this.isSelected())
        {
            Cursor min = this.getMin();

            if (shift)
            {
                min.offset = Math.max(min.offset - 4, 0);
            }

            Cursor temp = new Cursor();
            List<String> splits = UITextEditor.splitNewlineString(this.getSelectedText());

            for (int i = 0; i < splits.size(); i++)
            {
                if (shift)
                {
                    int indent = this.getIndent(splits.get(i));

                    splits.set(i, splits.get(i).substring(Math.min(indent, 4)));
                }
                else
                {
                    splits.set(i, "    " + splits.get(i));
                }
            }

            String result = String.join("\n", splits);

            temp.copy(min);
            this.deleteSelection();
            this.writeString(result);
            this.getMin().set(min.line, splits.get(splits.size() - 1).length());
            min.copy(temp);

            if (!shift)
            {
                min.offset += 4;
            }

            undo.postText = result;
        }
        else
        {
            undo.postText = "    ";

            this.deleteSelection();
            this.deselect();
            this.writeString(undo.postText);
        }
    }

    public int getIndent(int i)
    {
        if (this.hasLine(i))
        {
            return this.getIndent(this.text.get(i).text);
        }

        return 0;
    }

    public int getIndent(String line)
    {
        for (int j = 0; j < line.length(); j++)
        {
            char c = line.charAt(j);

            if (c != ' ')
            {
                return j;
            }
        }

        return line.length();
    }

    public String createIndent(int i)
    {
        StringBuilder builder = new StringBuilder();

        while (i > 0)
        {
            builder.append(' ');

            i -= 1;
        }

        return builder.toString();
    }

    /* Replacing rendering */

    @Override
    protected int renderTextLine(FontRenderer font, VAOBuilder builder, String line, int index, int i, int j, int nx, int ny)
    {
        /* Cache line number to be later rendered in drawForeground() */
        if (this.lines && j == 0)
        {
            String label = String.valueOf(i + 1);

            int x = this.area.x + 5 + this.placements - font.getWidth(label);

            if (this.lineNumber >= this.numbers.size())
            {
                this.numbers.add(new TextLineNumber());
            }

            this.numbers.get(this.lineNumber).set(label, x, ny);
            this.lineNumber += 1;
        }

        /* Draw  */
        HighlightedTextLine textLine = this.text.get(i);

        if (textLine.segments == null)
        {
            textLine.setSegments(this.highlighter.parse(font, this.text, textLine.text, i));

            if (textLine.wrappedLines != null)
            {
                textLine.calculateWrappedSegments(font);
            }
        }

        List<TextSegment> segments = textLine.segments;

        if (textLine.wrappedSegments != null)
        {
            segments = j < textLine.wrappedSegments.size() ? textLine.wrappedSegments.get(j) : null;
        }

        if (segments != null)
        {
            boolean shadow = this.highlighter.getStyle().shadow;

            for (TextSegment segment : segments)
            {
                index = font.build(builder, segment.text, nx, ny, index, segment.color, shadow);

                nx += segment.width;
            }
        }

        return index;
    }

    @Override
    protected int getShiftX()
    {
        return this.lines ? 10 + this.placements - 1 : 0;
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        this.area.render(context.batcher, Colors.A100 | Colors.mulRGB(this.highlighter.getStyle().background, 0.8F));
    }

    @Override
    protected void renderForeground(FontRenderer font, UIContext context)
    {
        if (this.lines)
        {
            /* Draw line numbers background */
            int x = this.area.x + this.getShiftX();

            context.batcher.box(this.area.x, this.area.y, x, this.area.ey(), Colors.A100 | this.highlighter.getStyle().background);

            /* Draw cached line numbers */
            for (TextLineNumber number : this.numbers)
            {
                if (!number.render)
                {
                    break;
                }

                context.batcher.text(font, number.line, number.x, number.y, this.highlighter.getStyle().lineNumbers);
                number.render = false;
            }

            this.lineNumber = 0;

            /* Draw shadow to the right of line numbers when scrolling */
            float a = Math.min(this.horizontal.scroll / 10F, 1F) * 0.25F;

            if (a > 0)
            {
                context.batcher.gradientHBox(x, this.area.y, x + 10, this.area.ey(), Colors.a(a), 0);
            }
        }
    }
}