package mchorse.bbs.ui.framework.elements.input.text.undo;

import mchorse.bbs.ui.framework.elements.input.text.UITextarea;
import mchorse.bbs.ui.framework.elements.input.text.utils.Cursor;
import mchorse.bbs.utils.undo.IUndo;

public class TextEditUndo implements IUndo<UITextarea>
{
    /**
     * Text that was present before undo
     */
    public String text;

    /* Cursor and selection before a text operation */
    public Cursor cursor = new Cursor(-1, 0);
    public Cursor selection = new Cursor(-1, 0);

    /**
     * Text that will be added
     */
    public String postText = "";

    /* Cursor and selection after a text operation */
    public Cursor postCursor = new Cursor(-1, 0);
    public Cursor postSelection = new Cursor(-1, 0);

    public boolean ready;

    public TextEditUndo(UITextarea element)
    {
        this(element.getSelectedText(), element.cursor, element.selection);
    }

    public TextEditUndo(String text, Cursor cursor, Cursor selection)
    {
        this.text = text;
        this.cursor.copy(cursor);
        this.selection.copy(selection);
    }

    public void post(String postText, Cursor postCursor, Cursor postSelection)
    {
        this.postText = postText;
        this.postCursor.copy(postCursor);
        this.postSelection.copy(postSelection);
    }

    public TextEditUndo ready()
    {
        this.ready = true;

        return this;
    }

    @Override
    public IUndo<UITextarea> noMerging()
    {
        return this;
    }

    @Override
    public boolean isMergeable(IUndo<UITextarea> undo)
    {
        if (undo instanceof TextEditUndo)
        {
            TextEditUndo text = (TextEditUndo) undo;

            if (this.getType() == text.getType() && text.getType() != UndoType.REPLACE)
            {
                /* Vertical merging can't be done */
                if (this.postCursor.line != text.postCursor.line)
                {
                    return false;
                }

                /* Verifying insert undo which only makes sense if the the next
                 * offset is exactly X character(s) away from previous cursor */
                if (this.getType() == UndoType.INSERT && text.cursor.offset != this.cursor.offset + this.postText.length())
                {
                    return false;
                }

                if (this.getType() == UndoType.DELETE)
                {
                    if (this.isBackspace() && text.isBackspace())
                    {
                        /* Backspace merging only works when cursor at the same place */
                        if (!this.cursor.isEqualTo(text.cursor))
                        {
                            return false;
                        }
                    }
                    /* Verifying delete undo which only makes sense if the the next
                     * offset is exactly X character(s) away from previous cursor */
                    else if (text.cursor.offset != this.cursor.offset - this.text.length())
                    {
                        return false;
                    }
                }

                /* Undos involving selections can't be merged either */
                return this.isBackspace() == text.isBackspace() && !(text.wasSelecting() || this.wasSelecting());
            }
        }

        return false;
    }

    @Override
    public void merge(IUndo<UITextarea> undo)
    {
        TextEditUndo text = (TextEditUndo) undo;

        if (text.getType() == UndoType.INSERT)
        {
            this.mergeInsert(text);
        }
        else if (text.getType() == UndoType.DELETE)
        {
            this.mergeDelete(text);
        }
    }

    /**
     * Handle insert merging (simple)
     */
    private void mergeInsert(TextEditUndo text)
    {
        this.postCursor.copy(text.postCursor);
        this.postSelection.copy(text.postSelection);
        this.postText += text.postText;
    }

    /**
     * Handle delete merging (simple)
     */
    private void mergeDelete(TextEditUndo text)
    {
        if (this.isBackspace())
        {
            this.postCursor.copy(text.postCursor);
            this.postSelection.copy(text.postSelection);
            this.text += text.text;
        }
        else
        {
            this.postCursor.copy(text.postCursor);
            this.postSelection.copy(text.postSelection);
            this.text = text.text + this.text;
        }
    }

    /* If being completely honest, I don't understand how these undo
     * and redo method works... but they works, at least most of the
     * time... */

    @Override
    public void undo(UITextarea element)
    {
        element.cursor.copy(this.cursor);
        element.selection.copy(this.selection);

        UndoType type = this.getType();

        if (type == UndoType.REPLACE || type == UndoType.INSERT)
        {
            if (element.selection.isThisLessTo(element.cursor))
            {
                element.swapSelection();
            }

            element.selectTextful(this.postText, false);
            element.deleteSelection();
        }

        if (type == UndoType.REPLACE || type == UndoType.DELETE)
        {
            if (!this.wasSelecting() || type == UndoType.DELETE)
            {
                element.cursor.copy(this.postCursor);
                element.selection.copy(this.postSelection);
            }

            element.writeString(this.text);
        }

        element.cursor.copy(this.cursor);
        element.selection.copy(this.selection);
    }

    @Override
    public void redo(UITextarea element)
    {
        element.cursor.copy(this.cursor);
        element.selection.copy(this.selection);

        UndoType type = this.getType();

        if (type == UndoType.REPLACE || type == UndoType.DELETE)
        {
            if (element.cursor.isThisLessTo(element.selection))
            {
                element.swapSelection();
            }

            if (element.isSelected())
            {
                element.deleteSelection();
            }
            else
            {
                /* Handle delete key deletion */
                boolean backspace = this.isBackspace();

                for (int i = 0; i < this.text.length(); i++)
                {
                    if (backspace)
                    {
                        element.moveCursor(1, 0);
                    }

                    element.deleteCharacter();
                }
            }
        }

        if (type == UndoType.REPLACE || type == UndoType.INSERT)
        {
            element.writeString(this.postText);
        }

        element.cursor.copy(this.postCursor);
        element.selection.copy(this.postSelection);
    }

    public boolean isBackspace()
    {
        return this.getType() == UndoType.DELETE && this.cursor.isEqualTo(this.postCursor);
    }

    public UndoType getType()
    {
        if (!this.text.isEmpty() && this.postText.isEmpty())
        {
            return UndoType.DELETE;
        }
        else if (this.text.isEmpty() && !this.postText.isEmpty())
        {
            return UndoType.INSERT;
        }

        return UndoType.REPLACE;
    }

    public boolean wasSelecting()
    {
        return !this.selection.isEmpty();
    }

    public static enum UndoType
    {
        REPLACE, DELETE, INSERT
    }
}