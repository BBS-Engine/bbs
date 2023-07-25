package mchorse.bbs.ui.framework.elements.input.text;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.IFocusedUIElement;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.undo.TextEditUndo;
import mchorse.bbs.ui.framework.elements.input.text.utils.Cursor;
import mchorse.bbs.ui.framework.elements.input.text.utils.TextLine;
import mchorse.bbs.ui.framework.elements.utils.ITextColoring;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.ScrollArea;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.StringGroupMatcher;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.utils.undo.UndoManager;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UITextarea <T extends TextLine> extends UIElement implements IFocusedUIElement, ITextColoring
{
    public ScrollArea horizontal = new ScrollArea(new Area());
    public ScrollArea vertical = new ScrollArea(this.area);

    public Consumer<String> callback;

    /* Visual properties */
    private boolean background;
    protected int padding = 10;
    protected int lineHeight = 12;
    protected int textColor = Colors.WHITE;
    protected boolean textShadow;
    protected boolean wrapping;

    /* Editing */
    private boolean focused;
    private int dragging;
    protected List<T> text = new ArrayList<T>();
    public final Cursor cursor = new Cursor();
    public final Cursor selection = new Cursor(-1, 0);

    /* Last mouse position */
    private int lastMX;
    private int lastMY;
    private long lastClick;

    /* Callback update (to avoid joining a huge array of text every keystroke) */
    private long update;
    private long lastUpdate;

    private UndoManager<UITextarea> undo;

    private int lastW;

    public static List<String> splitNewlineString(String string)
    {
        List<String> splits = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();

        for (int i = 0, c = string.length(); i < c; i++)
        {
            char character = string.charAt(i);

            if (character == '\n')
            {
                splits.add(builder.toString());
                builder = new StringBuilder();
            }
            else
            {
                builder.append(character);
            }
        }

        splits.add(builder.toString());

        return splits;
    }

    public UITextarea(Consumer<String> callback)
    {
        super();

        this.callback = callback;

        this.horizontal.direction = ScrollDirection.HORIZONTAL;
        this.horizontal.cancelScrollEdge = true;
        this.horizontal.scrollSpeed = this.lineHeight * 2;
        this.vertical.cancelScrollEdge = true;
        this.vertical.scrollSpeed = this.lineHeight * 2;

        this.clear();
    }

    protected FontRenderer getFont()
    {
        return BBS.getRender().getFont();
    }

    public UITextarea<T> background()
    {
        return this.background(true);
    }

    public UITextarea<T> background(boolean background)
    {
        this.background = background;

        return this;
    }

    public UITextarea<T> padding(int padding)
    {
        this.padding = padding;

        return this;
    }

    public UITextarea<T> lineHeight(int lineHeight)
    {
        this.lineHeight = lineHeight;

        return this;
    }

    public UITextarea<T> wrap()
    {
        return this.wrap(!this.wrapping);
    }

    public UITextarea<T> wrap(boolean wrapping)
    {
        this.wrapping = wrapping;

        return this;
    }

    @Override
    public void setColor(int textColor, boolean textShadow)
    {
        this.textColor = textColor;
        this.textShadow = textShadow;
    }

    public void setText(String text)
    {
        this.text.clear();

        for (String line : text.split("\n"))
        {
            this.text.add(this.createTextLine(line));
        }

        this.cursor.set(0, 0);
        this.selection.set(-1, 0);
        this.horizontal.scroll = 0;
        this.vertical.scroll = 0;
        this.undo = new UndoManager<UITextarea>(100).simpleMerge();

        if (this.area.w > 0)
        {
            this.recalculateWrapping();
            this.recalculateSizes();
        }
    }

    protected T createTextLine(String line)
    {
        return (T) new TextLine(line);
    }

    public String getText()
    {
        return this.text.stream().map(t -> t.text).collect(Collectors.joining("\n"));
    }

    public List<T> getLines()
    {
        return this.text;
    }

    public int getWrappedWidth()
    {
        return this.area.w - this.padding * 3 - this.getShiftX();
    }

    /* Selection API */

    public boolean isSelected()
    {
        return !this.selection.isEmpty();
    }

    public void startSelecting()
    {
        this.selection.copy(this.cursor);
    }

    public void deselect()
    {
        this.selection.set(-1, 0);
    }

    public void swapSelection()
    {
        if (this.isSelected())
        {
            Cursor temp = new Cursor();

            temp.copy(this.selection);
            this.selection.copy(this.cursor);
            this.cursor.copy(temp);
        }
    }

    public void selectAll()
    {
        this.cursor.set(0, 0);

        this.startSelecting();
        this.cursor.line = this.text.size() - 1;
        this.moveCursorToLineEnd();
    }

    public String getSelectedText()
    {
        if (!this.isSelected())
        {
            return "";
        }

        return this.getText(this.cursor, this.selection);
    }

    public String getText(Cursor a, Cursor b)
    {
        StringJoiner joiner = new StringJoiner("\n");

        Cursor min = a.isThisLessTo(b) ? a : b;
        Cursor max = a.isThisLessTo(b) ? b : a;

        for (int i = min.line; i <= Math.min(max.line, this.text.size() - 1); i++)
        {
            String line = this.text.get(i).text;

            if (i == min.line && i == max.line)
            {
                joiner.add(line.substring(min.getOffset(line), max.getOffset(line)));
            }
            else if (i == min.line)
            {
                joiner.add(min.end(line));
            }
            else if (i == max.line)
            {
                joiner.add(max.start(line));
            }
            else
            {
                joiner.add(line);
            }
        }

        return joiner.toString();
    }

    public boolean selectGroup(int direction, boolean select)
    {
        Pair<Cursor, Cursor> group = this.findGroup(direction, this.cursor);

        if (group == null)
        {
            return false;
        }

        Cursor min = group.a;
        Cursor max = group.b;

        if (select)
        {
            if (direction == 0)
            {
                this.cursor.offset = max.offset;
                this.selection.set(this.cursor.line, min.offset);
            }
            else
            {
                if (!this.isSelected())
                {
                    this.selection.copy(this.cursor);
                }

                this.cursor.offset = direction < 0 ? min.offset : max.offset;
            }
        }
        else
        {
            this.deselect();
            this.cursor.offset = direction < 0 ? min.offset : max.offset;
        }

        return true;
    }

    public int measureGroup(int direction, Cursor cursor)
    {
        if (direction == 0)
        {
            return 0;
        }

        Pair<Cursor, Cursor> group = this.findGroup(direction, cursor);

        if (group == null)
        {
            return 0;
        }

        Cursor other = direction < 0 ? group.a : group.b;

        return other.offset - cursor.offset;
    }

    /**
     * Find a group (two cursors) at given cursor
     */
    public Pair<Cursor, Cursor> findGroup(int direction, Cursor cursor)
    {
        StringGroupMatcher matcher = new StringGroupMatcher();
        Pair<Integer, Integer> group = matcher.findGroup(direction, this.text.get(cursor.line).text, cursor.offset);

        if (group == null)
        {
            return null;
        }

        return new Pair<Cursor, Cursor>(new Cursor(cursor.line, group.a), new Cursor(cursor.line, group.b));
    }

    /**
     * Select only a textful >:)
     */
    public boolean selectTextful(String text, boolean reverse)
    {
        this.deselect();

        List<String> splits = splitNewlineString(text);

        this.selection.copy(this.cursor);

        for (int i = 0; i < splits.size(); i++)
        {
            String line = this.text.get(this.selection.line).text;
            int l = splits.get(reverse ? splits.size() - (i + 1) : i).length();

            this.selection.offset += (reverse ? -l : l);

            if (i < splits.size() - 1)
            {
                if (reverse && this.selection.offset < 0)
                {
                    return false;
                }
                else if (!reverse && this.selection.offset + l < line.length())
                {
                    return false;
                }

                this.selection.line += reverse ? -1 : 1;
                this.selection.offset = reverse ? this.text.get(this.selection.line).text.length() : 0;
            }
        }

        return true;
    }

    public void checkSelection(boolean selecting)
    {
        if (selecting && !this.isSelected())
        {
            this.startSelecting();
        }
        else if (!selecting && this.isSelected())
        {
            this.deselect();
        }
    }

    /* Writing API */

    public void clear()
    {
        this.setText("");
    }

    protected void changedLine(int i)
    {
        this.calculateWrappedLine(this.text.get(i));
        this.recalculateSizes();
    }

    protected void changedLineAfter(int i)
    {
        while (i < this.text.size())
        {
            this.calculateWrappedLine(this.text.get(i));

            i += 1;
        }

        this.recalculateSizes();
    }

    public void writeNewLine()
    {
        if (!this.hasLine(this.cursor.line))
        {
            return;
        }

        String line = this.text.get(this.cursor.line).text;

        if (this.cursor.offset == 0 || line.isEmpty())
        {
            this.text.add(this.cursor.line, this.createTextLine(""));
        }
        else if (this.cursor.offset >= line.length())
        {
            this.text.add(this.cursor.line + 1, this.createTextLine(""));
        }
        else
        {
            this.text.get(this.cursor.line).set(this.cursor.start(line));
            this.text.add(this.cursor.line + 1, this.createTextLine(this.cursor.end(line)));
            this.moveCursorToLineStart();
        }

        this.changedLineAfter(this.cursor.line);

        this.cursor.line += 1;
        this.cursor.offset = 0;
    }

    public void writeCharacter(String character)
    {
        if (this.hasLine(this.cursor.line))
        {
            String line = this.text.get(this.cursor.line).text;
            int index = this.cursor.offset;

            if (index >= line.length())
            {
                line += character;
            }
            else if (index == 0)
            {
                line = character + line;
            }
            else
            {
                line = this.cursor.start(line) + character + this.cursor.end(line);
            }

            this.text.get(this.cursor.line).set(line);
            this.changedLine(this.cursor.line);
        }
    }

    public void writeString(String string)
    {
        List<String> splits = splitNewlineString(string);
        int size = splits.size();

        if (size == 1)
        {
            this.writeCharacter(string);
            this.cursor.offset += string.length();
        }
        else
        {
            int line = this.cursor.line;
            String remainder = this.cursor.end(this.text.get(line).text);

            this.text.get(line).set(this.cursor.start(this.text.get(line).text));

            for (int i = 0; i < size; i++)
            {
                if (i != 0 && i <= size - 1)
                {
                    this.cursor.line += 1;

                    this.moveCursorToLineStart();
                    this.text.add(this.cursor.line, this.createTextLine(""));
                }

                this.writeCharacter(splits.get(i));
            }

            this.cursor.offset = splits.get(size - 1).length();
            this.writeCharacter(remainder);
            this.changedLineAfter(line);
        }
    }

    public void pasteText(String text)
    {
        TextEditUndo undo = new TextEditUndo(this);

        this.deleteSelection();
        this.writeString(text);

        undo.ready().post(text, this.cursor, this.selection);
        this.undo.pushUndo(undo);
    }

    public String deleteCharacter()
    {
        if (this.hasLine(this.cursor.line))
        {
            String line = this.text.get(this.cursor.line).text;
            int index = Math.min(this.cursor.offset, line.length());

            if (line.isEmpty())
            {
                if (this.cursor.line > 0)
                {
                    this.text.remove(this.cursor.line);

                    this.cursor.line -= 1;

                    this.moveCursorToLineEnd();
                    this.changedLineAfter(this.cursor.line);

                    return "\n";
                }
            }
            else if (index >= line.length())
            {
                String deleted = line.substring(line.length() - 1);

                line = line.substring(0, line.length() - 1);
                this.text.get(this.cursor.line).set(line);
                this.moveCursorToLineEnd();

                this.changedLine(this.cursor.line);

                return deleted;
            }
            else if (index == 0)
            {
                if (this.cursor.line > 0)
                {
                    String text = this.text.remove(this.cursor.line).text;

                    this.cursor.line -= 1;

                    this.moveCursorToLineEnd();
                    this.text.get(this.cursor.line).text = this.text.get(this.cursor.line).text + text;
                    this.changedLineAfter(this.cursor.line);

                    return "\n";
                }
            }
            else
            {
                String deleted = line.substring(this.cursor.getOffset(line, -1), this.cursor.getOffset(line));

                line = this.cursor.start(line, -1) + this.cursor.end(line);
                this.text.get(this.cursor.line).text = line;
                this.moveCursor(-1, 0);
                this.changedLine(this.cursor.line);

                return deleted;
            }
        }

        return "";
    }

    public void deleteSelection()
    {
        if (!this.isSelected())
        {
            return;
        }

        Cursor min = this.getMin();
        Cursor max = this.getMax();

        if (min.line == max.line)
        {
            String line = this.text.get(min.line).text;

            if (min.offset <= 0 && max.offset >= line.length())
            {
                this.text.get(min.line).set("");
            }
            else
            {
                this.text.get(min.line).set(min.start(line) + max.end(line));
            }
        }
        else
        {
            String end = "";

            for (int i = max.line; i >= min.line; i--)
            {
                String line = this.text.get(i).text;

                if (i == max.line)
                {
                    end = max.end(line);
                    this.text.remove(i);
                }
                else if (i == min.line)
                {
                    this.text.get(i).set(min.start(line) + end);
                }
                else
                {
                    this.text.remove(i);
                }
            }
        }

        this.changedLineAfter(min.line);
        this.cursor.copy(min);
        this.deselect();
    }

    public boolean hasLine(int line)
    {
        return line >= 0 && line < this.text.size();
    }

    public Cursor getMin()
    {
        return this.selection.isThisLessTo(this.cursor) ? this.selection : this.cursor;
    }

    public Cursor getMax()
    {
        return this.selection.isThisLessTo(this.cursor) ? this.cursor : this.selection;
    }

    /* Moving cursor API */

    public void moveCursor(int x, int y)
    {
        this.moveCursor(x, y, true);
    }

    public void moveCursor(int x, int y, boolean jumpLine)
    {
        if (!this.hasLine(this.cursor.line))
        {
            return;
        }

        String line = this.text.get(this.cursor.line).text;

        if (x != 0)
        {
            int nx = this.cursor.offset + (x > 0 ? 1 : -1);

            if (nx < 0)
            {
                if (jumpLine)
                {
                    if (this.hasLine(this.cursor.line - 1))
                    {
                        this.cursor.line -= 1;
                        this.moveCursorToLineEnd();
                    }
                }
                else
                {
                    this.moveCursorToLineStart();
                }
            }
            else if (nx > line.length())
            {
                if (jumpLine)
                {
                    if (this.hasLine(this.cursor.line + 1))
                    {
                        this.cursor.line += 1;
                        this.moveCursorToLineStart();
                    }
                }
                else
                {
                    this.moveCursorToLineEnd();
                }
            }
            else
            {
                this.cursor.offset = nx;
            }
        }

        if (y != 0)
        {
            int ny = this.cursor.line + (y > 0 ? 1 : -1);

            if (this.hasLine(ny))
            {
                this.cursor.line = ny;
                this.cursor.offset = MathUtils.clamp(this.cursor.offset, 0, this.text.get(this.cursor.line).text.length());
            }
        }
    }

    public void moveCursorToLineStart()
    {
        this.cursor.offset = 0;
    }

    public void moveCursorToLineEnd()
    {
        if (this.hasLine(this.cursor.line))
        {
            this.cursor.offset = this.text.get(this.cursor.line).text.length();
        }
    }

    public void moveCursorTo(FontRenderer font, Cursor cursor, int x, int y)
    {
        x -= this.area.x + this.padding;
        y -= this.area.y + this.padding;

        x += this.horizontal.scroll - this.getShiftX();
        y += this.vertical.scroll;

        /* Wrapped and unwrapped move to cursor require two different versions
         * of the same operation due to complexity of wrapped data structure */
        if (this.wrapping)
        {
            this.moveToCursorWrapped(font, cursor, x, y);
        }
        else
        {
            this.moveCursorToUnwrapped(font, cursor, x, y);
        }
    }

    private void moveToCursorWrapped(FontRenderer font, Cursor cursor, int x, int y)
    {
        if (this.text.isEmpty())
        {
            return;
        }

        T current = null;
        int line = y < 0 ? 0 : y / this.lineHeight;
        int l = 0;
        int s = 0;

        for (int i = 0, c = this.text.size(); i < c; i++)
        {
            T textLine = this.text.get(i);

            if (line >= l && line < l + textLine.getLines())
            {
                current = textLine;
                cursor.line = i;
                s = line - l;

                break;
            }

            l += textLine.getLines();
        }

        if (current == null)
        {
            current = this.text.get(this.text.size() - 1);
            cursor.line = this.text.size() - 1;
            s = current.getLines() - 1;
        }

        cursor.offset = 0;

        String lineText = current.text;

        if (current.wrappedLines != null)
        {
            for (int i = 0; i < s; i++)
            {
                cursor.offset += current.wrappedLines.get(i).length();
            }

            lineText = current.wrappedLines.get(s);
        }

        int w = 0;

        if (x > font.getWidth(lineText))
        {
            cursor.offset += lineText.length();

            return;
        }
        else if (x < 0)
        {
            return;
        }

        int i = 0;

        while (x > w)
        {
            w = font.getWidth(lineText.substring(0, i));

            cursor.offset += 1;
            i += 1;
        }

        if (cursor.offset > 0)
        {
            cursor.offset -= 2;
        }
    }

    private void moveCursorToUnwrapped(FontRenderer font, Cursor cursor, int x, int y)
    {
        cursor.line = MathUtils.clamp(y / this.lineHeight, 0, this.text.size() - 1);

        String line = this.text.get(cursor.line).text;
        int w = font.getWidth(line);

        if (x <= 0)
        {
            this.moveCursorToLineStart();
        }
        else if (x > w)
        {
            this.moveCursorToLineEnd();
        }
        else
        {
            cursor.offset = 0;
            w = font.getWidth(cursor.start(line));

            while (x > w)
            {
                w = font.getWidth(cursor.start(line, 1));

                cursor.offset += 1;
            }

            if (cursor.offset > 0)
            {
                cursor.offset -= 1;
            }
        }
    }

    public void moveViewportToCursor()
    {
        if (!this.hasLine(this.cursor.line))
        {
            return;
        }

        Vector2d pos = this.getCursorPosition(this.getFont(), this.cursor);

        pos.x += this.horizontal.scroll;
        pos.y += this.vertical.scroll;

        int w = 4;
        int h = this.lineHeight;

        this.horizontal.scrollIntoView((int) pos.x, w + this.padding * 2, this.getShiftX());
        this.vertical.scrollIntoView((int) pos.y, h + this.padding * 2, this.getShiftX());
    }

    /* Focusable */

    @Override
    public boolean isFocused()
    {
        return this.focused;
    }

    @Override
    public void focus(UIContext context)
    {
        this.focused = true;
    }

    @Override
    public void unfocus(UIContext context)
    {
        this.focused = false;
    }

    @Override
    public void selectAll(UIContext context)
    {
        this.selectAll();
    }

    @Override
    public void unselect(UIContext context)
    {
        this.deselect();
    }

    /* GUI input handling */

    @Override
    public void resize()
    {
        super.resize();

        if (this.lastW != this.area.w)
        {
            this.lastW = this.area.w;

            this.recalculateWrapping();
        }

        this.recalculateSizes();
        this.horizontal.clamp();
        this.vertical.clamp();
    }

    public void recalculate()
    {
        for (T textLine : this.text)
        {
            this.calculateWrappedLine(textLine);
        }

        this.recalculateSizes();
    }

    protected void recalculateWrapping()
    {
        if (this.wrapping)
        {
            for (T textLine : this.text)
            {
                this.calculateWrappedLine(textLine);
            }
        }
    }

    protected void calculateWrappedLine(T textLine)
    {
        if (this.wrapping)
        {
            textLine.calculateWrappedLines(this.getFont(), this.getWrappedWidth());
        }
        else
        {
            textLine.resetWrapping();
        }
    }

    protected void recalculateSizes()
    {
        int w = 0;
        int h = 0;

        for (T textLine : this.text)
        {
            if (!this.wrapping)
            {
                w = Math.max(this.getFont().getWidth(textLine.text), w);
            }

            h += textLine.getLines() * this.lineHeight;
        }

        int offset = this.getShiftX();

        this.horizontal.area.copy(this.area);
        this.horizontal.area.x += offset;
        this.horizontal.area.w -= offset;
        this.horizontal.scrollSize = this.wrapping ? w : this.getHorizontalSize(w);

        this.vertical.scrollSize = h - (this.lineHeight - this.getFont().getHeight()) + this.padding * 2;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.horizontal.mouseClicked(context) || this.vertical.mouseClicked(context))
        {
            return true;
        }

        boolean wasFocused = this.focused;
        boolean shift = Window.isShiftPressed();

        this.focused = this.area.isInside(context);

        if (this.focused)
        {
            if (context.mouseButton == 0)
            {
                if (System.currentTimeMillis() < this.lastClick)
                {
                    this.selectGroup(0, true);
                    this.lastClick -= 500;
                }
                else
                {
                    if (!shift)
                    {
                        this.deselect();

                        this.dragging = 1;
                    }
                    else if (!this.isSelected())
                    {
                        this.startSelecting();
                    }

                    this.moveCursorTo(this.getFont(), this.cursor, context.mouseX, context.mouseY);
                    this.lastClick = System.currentTimeMillis() + 200;
                }
            }
            else if (context.mouseButton == 2)
            {
                this.dragging = 3;
            }

            this.lastMX = context.mouseX;
            this.lastMY = context.mouseY;
        }

        if (wasFocused != this.focused)
        {
            context.focus(wasFocused ? null : this);
        }

        return this.focused && context.mouseButton != 1;
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        if (Window.isShiftPressed())
        {
            return this.horizontal.mouseScroll(context);
        }

        return this.vertical.scrollSize >= this.area.h && this.vertical.mouseScroll(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.horizontal.mouseReleased(context);
        this.vertical.mouseReleased(context);
        this.dragging = 0;

        return super.subMouseReleased(context);
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (!this.focused)
        {
            return false;
        }

        if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            context.unfocus();

            return true;
        }

        boolean ctrl = Window.isCtrlPressed();
        boolean shift = Window.isShiftPressed();

        TextEditUndo undo = new TextEditUndo(this);

        if (this.handleKeys(context, undo, ctrl, shift))
        {
            this.moveViewportToCursor();
        }

        if (undo.ready)
        {
            this.undo.pushUndo(undo);
        }

        this.update = context.getTick() + 20;
        this.horizontal.clamp();
        this.vertical.clamp();

        return super.subKeyPressed(context);
    }

    @Override
    public boolean subTextInput(UIContext context)
    {
        if (this.focused && this.getFont().hasCharacter(context.getInputCharacter()))
        {
            TextEditUndo undo = new TextEditUndo(this);
            String character = this.getFromChar(context.getInputCharacter());

            if (!character.isEmpty())
            {
                this.deleteSelection();
                this.deselect();
                this.writeCharacter(character);
                this.moveCursor(1, 0);

                undo.ready().post(character, this.cursor, this.selection);
                this.playSound("input");

                this.moveViewportToCursor();
                this.undo.pushUndo(undo);
            }

            return true;
        }

        return false;
    }

    /**
     * Handle multiline text editor keybinds
     */
    protected boolean handleKeys(UIContext context, TextEditUndo undo, boolean ctrl, boolean shift)
    {
        /* Undo/redo */
        if (ctrl && context.isPressed(GLFW.GLFW_KEY_Z))
        {
            boolean result = this.undo.undo(this);

            if (result)
            {
                this.playSound("undo");
            }

            return result;
        }
        else if (ctrl && context.isPressed(GLFW.GLFW_KEY_Y))
        {
            boolean result = this.undo.redo(this);

            if (result)
            {
                this.playSound("redo");
            }

            return result;
        }
        /* Select all */
        else if (ctrl && context.isPressed(GLFW.GLFW_KEY_A))
        {
            this.selectAll();
        }
        /* Cursor and navigation */
        else if (context.isHeld(GLFW.GLFW_KEY_UP) || context.isHeld(GLFW.GLFW_KEY_DOWN) || context.isHeld(GLFW.GLFW_KEY_RIGHT) || context.isHeld(GLFW.GLFW_KEY_LEFT))
        {
            int x = context.isHeld(GLFW.GLFW_KEY_RIGHT) ? 1 : (context.isHeld(GLFW.GLFW_KEY_LEFT) ? -1 : 0);
            int y = context.isHeld(GLFW.GLFW_KEY_UP) ? -1 : (context.isHeld(GLFW.GLFW_KEY_DOWN) ? 1 : 0);

            if (x != 0 && ctrl)
            {
                if (!this.selectGroup(x, shift))
                {
                    this.checkSelection(shift);
                    this.moveCursor(x, 0);
                }
            }
            else
            {
                this.checkSelection(shift);
                this.moveCursor(x, y);
            }

            this.playSound("move");

            return true;
        }
        else if (context.isPressed(GLFW.GLFW_KEY_HOME))
        {
            this.checkSelection(shift);
            this.moveCursorToLineStart();

            this.playSound("jump_beginning");

            return true;
        }
        else if (context.isPressed(GLFW.GLFW_KEY_END))
        {
            this.checkSelection(shift);
            this.moveCursorToLineEnd();

            this.playSound("jump_end");

            return true;
        }
        /* Copy, cut and paste */
        else if (ctrl && (context.isPressed(GLFW.GLFW_KEY_C) || context.isPressed(GLFW.GLFW_KEY_X)) && this.isSelected())
        {
            Window.setClipboard(this.getSelectedText());

            if (context.isPressed(GLFW.GLFW_KEY_X))
            {
                this.deleteSelection();
                this.deselect();

                undo.ready().post("", this.cursor, this.selection);
                this.playSound("cut");
            }
            else
            {
                this.playSound("copy");
            }

            return context.isPressed(GLFW.GLFW_KEY_X);
        }
        else if (ctrl && context.isPressed(GLFW.GLFW_KEY_V))
        {
            String pasted = Window.getClipboard();

            this.deleteSelection();
            this.deselect();
            this.writeString(pasted);

            undo.ready().post(pasted, this.cursor, this.selection);
            this.playSound("paste");

            return true;
        }
        /* Text input */
        else if (context.isPressed(GLFW.GLFW_KEY_TAB))
        {
            this.keyTab(shift, undo.ready());
            undo.post(undo.postText, this.cursor, this.selection);
            this.playSound(shift ? "untab" : "tab");

            return true;
        }
        else if (context.isHeld(GLFW.GLFW_KEY_ENTER))
        {
            this.keyNewLine(undo.ready());
            undo.post(undo.postText, this.cursor, this.selection);
            this.playSound("newline");

            return true;
        }
        else if (context.isHeld(GLFW.GLFW_KEY_BACKSPACE) || context.isHeld(GLFW.GLFW_KEY_DELETE))
        {
            boolean delete = context.isHeld(GLFW.GLFW_KEY_DELETE);

            if (this.isSelected())
            {
                this.deleteSelection();
                this.deselect();

                this.playSound("delete_selection");
            }
            else
            {
                if (delete)
                {
                    int measure = ctrl ? Math.max(this.measureGroup(1, this.cursor), 1) : 1;

                    for (int i = 0; i < measure; i++)
                    {
                        this.moveCursor(1, 0);
                        undo.text = undo.text + this.deleteCharacter();
                    }
                }
                else
                {
                    this.keyBackspace(undo, ctrl);
                }

                this.playSound("delete");
            }

            undo.ready().post("", this.cursor, this.selection);

            return true;
        }

        return false;
    }

    protected void playSound(String event)
    {
        UIUtils.playClick();
    }

    protected String getFromChar(char typedChar)
    {
        return String.valueOf(typedChar);
    }

    protected void keyNewLine(TextEditUndo undo)
    {
        this.deleteSelection();
        this.deselect();
        this.writeNewLine();

        undo.postText += "\n";
    }

    protected void keyBackspace(TextEditUndo undo, boolean ctrl)
    {
        int measure = ctrl ? Math.max(Math.abs(this.measureGroup(-1, this.cursor)), 1) : 1;

        for (int i = 0; i < measure; i++)
        {
            undo.text = this.deleteCharacter() + undo.text;
        }
    }

    protected void keyTab(boolean shift, TextEditUndo undo)
    {
        undo.ready = false;

        this.getContext().focus(this, shift ? -1 : 1);
    }

    @Override
    public void render(UIContext context)
    {
        this.handleLogic(context);

        if (this.background)
        {
            this.renderBackground(context);
        }

        super.render(context);

        context.batcher.clip(this.area, context);

        int x = this.area.x + this.padding;
        int y = this.area.y + this.padding;

        boolean renderCursor = false;
        int cx = -1;
        int cy = -1;
        int index = 0;

        FontRenderer font = this.getFont();
        Cursor min = this.getMin();
        Cursor max = this.getMax();

        if (this.isSelected())
        {
            this.renderSelectionBar(font, context, x, y, min, max);
        }

        context.batcher.flush();

        Shader shader = context.render.getShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D);
        VAOBuilder builder = context.render.getVAO().setup(shader, VAO.INDICES).stack(context.render.stack);

        builder.begin();

        for (int i = 0, ci = this.text.size(); i < ci; i++)
        {
            T textLine = this.text.get(i);
            String line = textLine.text;
            int newX = x - this.horizontal.scroll + this.getShiftX();
            int newY = y - this.vertical.scroll;

            if (newY > this.area.ey())
            {
                break;
            }

            if (this.cursor.line == i && this.focused)
            {
                renderCursor = true;
            }

            int lines = textLine.getLines() - 1;

            if (newY + font.getHeight() + lines * this.lineHeight >= this.area.y)
            {
                int cursorW = 0;

                if (renderCursor)
                {
                    cursorW = line.isEmpty() ? 0 : font.getWidth(this.cursor.start(line));
                }

                if (textLine.wrappedLines == null)
                {
                    if (renderCursor && cx < 0)
                    {
                        cx = newX + cursorW;
                        cy = newY;
                    }

                    index = this.renderTextLine(font, builder, line, index, i, 0, newX, newY);
                }
                else
                {
                    int wrappedW = 0;

                    for (int j = 0, cj = textLine.wrappedLines.size(); j < cj; j++)
                    {
                        String wrappedLine = textLine.wrappedLines.get(j);
                        int lineW = font.getWidth(wrappedLine);
                        int lineY = newY + j * this.lineHeight;

                        if (renderCursor && cx < 0 && cursorW >= wrappedW && cursorW <= wrappedW + lineW)
                        {
                            cx = newX + cursorW - wrappedW;
                            cy = lineY;
                        }

                        index = this.renderTextLine(font, builder, wrappedLine, index, i, j, newX, lineY);
                        wrappedW += lineW;
                    }
                }
            }

            y += textLine.getLines() * this.lineHeight;
        }

        font.bindTexture(context.render);

        builder.render();

        if (renderCursor)
        {
            float a = (float) Math.sin(context.getTickTransition() / 2D);
            int c = Colors.setA(Colors.WHITE, a * 0.5F + 0.5F);

            context.batcher.box(cx, cy - 1, cx + 1, cy + font.getHeight() + 1, c);
        }

        this.horizontal.renderScrollbar(context.batcher);
        this.vertical.renderScrollbar(context.batcher);

        this.renderForeground(font, context);

        context.batcher.unclip(context);
    }

    protected int getShiftX()
    {
        return 0;
    }

    protected int getHorizontalSize(int w)
    {
        return w + this.padding * 2 + this.getShiftX();
    }

    protected int renderTextLine(FontRenderer font, VAOBuilder builder, String line, int index, int i, int j, int nx, int ny)
    {
        return font.build(builder, line, nx, ny, index, this.textColor, this.textShadow);
    }

    protected void renderBackground(UIContext context)
    {
        int borderColor = this.focused ? Colors.A100 | BBSSettings.primaryColor.get() : Colors.LIGHTER_GRAY;

        this.area.render(context.batcher, borderColor);
        this.area.render(context.batcher, Colors.A100, 1);
    }

    protected void renderForeground(FontRenderer font, UIContext context)
    {}

    /**
     * Handle dragging scrollbars and selecting text
     */
    private void handleLogic(UIContext context)
    {
        if (this.update > this.lastUpdate)
        {
            this.lastUpdate = this.update;

            if (this.callback != null)
            {
                this.callback.accept(this.getText());
            }
        }

        if (this.dragging == 1 && (Math.abs(context.mouseX - this.lastMX) > 4 || Math.abs(context.mouseY - this.lastMY) > 4))
        {
            this.startSelecting();
            this.dragging = 2;
        }

        if (this.focused && this.dragging == 2)
        {
            this.moveCursorTo(this.getFont(), this.cursor, context.mouseX, context.mouseY);
            this.moveViewportToCursor();
        }

        if (this.dragging == 3)
        {
            this.horizontal.scroll += this.lastMX - context.mouseX;
            this.horizontal.clamp();

            this.vertical.scroll += this.lastMY - context.mouseY;
            this.vertical.clamp();

            this.lastMX = context.mouseX;
            this.lastMY = context.mouseY;
        }

        this.horizontal.drag(context);
        this.vertical.drag(context);
    }

    /**
     * Draw background text selection
     */
    private void renderSelectionBar(FontRenderer font, UIContext context, int x, int y, Cursor min, Cursor max)
    {
        Vector2d minPos = this.getCursorPosition(font, min);
        Vector2d maxPos = this.getCursorPosition(font, max);

        this.renderSelectionArea(font, context, x + (int) minPos.x, y + (int) minPos.y, x + (int) maxPos.x, y + (int) maxPos.y);
    }

    protected Vector2d getCursorPosition(FontRenderer font, Cursor cursor)
    {
        Vector2d pos = new Vector2d();

        if (this.wrapping)
        {
            this.getCusrorPositionWrapped(font, cursor, pos);
        }
        else
        {
            String line = this.text.get(cursor.line).text;

            pos.x = font.getWidth(cursor.start(line));
            pos.y = cursor.line * this.lineHeight;
        }

        pos.x = pos.x - this.horizontal.scroll + this.getShiftX();
        pos.y = pos.y - this.vertical.scroll;

        return pos;
    }

    private void getCusrorPositionWrapped(FontRenderer font, Cursor cursor, Vector2d pos)
    {
        int lines = 0;
        int offset = 0;

        for (int i = 0, c = this.text.size(); i < c; i++)
        {
            T textLine = this.text.get(i);
            int textLines = textLine.getLines();

            if (i == cursor.line)
            {
                if (textLine.wrappedLines == null)
                {
                    offset = font.getWidth(cursor.start(textLine.text));
                }
                else
                {
                    int textOffset = 0;

                    for (int j = 0; j < textLine.wrappedLines.size(); j++)
                    {
                        String wrappedLine = textLine.wrappedLines.get(j);

                        if (cursor.offset >= textOffset && cursor.offset < textOffset + wrappedLine.length())
                        {
                            offset = font.getWidth(wrappedLine.substring(0, cursor.offset - textOffset));

                            break;
                        }

                        lines += 1;
                        textOffset += wrappedLine.length();
                    }

                    if (cursor.offset >= textLine.text.length())
                    {
                        lines -= 1;
                        offset = font.getWidth(textLine.wrappedLines.get(textLine.wrappedLines.size() - 1));
                    }
                }

                break;
            }

            lines += textLines;
        }

        pos.x = offset;
        pos.y = lines * this.lineHeight;
    }

    private void renderSelectionArea(FontRenderer font, UIContext context, int x1, int y1, int x2, int y2)
    {
        final int selectionPad = 2;
        int color = Colors.A50 | BBSSettings.primaryColor.get();

        boolean middle = y2 > y1 + this.lineHeight;
        boolean bottom = y2 > y1;

        int endX = bottom || middle ? this.area.ex() : x2 + selectionPad;
        int endY = bottom && !middle ? y2 : y1 + font.getHeight();

        if (!bottom && !middle)
        {
            endY += selectionPad;
        }

        context.batcher.box(x1 - selectionPad, y1 - selectionPad, endX, endY, color);

        if (middle)
        {
            context.batcher.box(this.area.x, y1 + font.getHeight(), this.area.ex(), y2, color);
        }

        if (bottom)
        {
            context.batcher.box(this.area.x, y2, x2 + selectionPad, y2 + font.getHeight() + selectionPad, color);
        }
    }
}