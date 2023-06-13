package mchorse.bbs.utils.undo;

import java.util.LinkedList;
import java.util.List;

/**
 * Undo manager
 *
 * This class is responsible for handling undo/redo functionality. In theory,
 * it can be used to practically with any data type.
 */
public class UndoManager<T>
{
    private List<IUndo<T>> undos = new LinkedList<IUndo<T>>();
    private int position = -1;

    private int limit = 20;
    private IUndoListener<T> callback;
    private boolean simpleMerge;

    public UndoManager()
    {}

    public UndoManager(int limit)
    {
        this.limit = limit;
    }

    public UndoManager(IUndoListener<T> callback)
    {
        this.callback = callback;
    }

    public UndoManager(int limit, IUndoListener<T> callback)
    {
        this.limit = limit;
        this.callback = callback;
    }

    public UndoManager<T> simpleMerge()
    {
        this.simpleMerge = true;

        return this;
    }

    public IUndoListener<T> getCallback()
    {
        return this.callback;
    }

    public void setCallback(IUndoListener<T> callback)
    {
        this.callback = callback;
    }

    /* Getters */

    public IUndo<T> getCurrentUndo()
    {
        if (this.position >= 0 && this.position < this.undos.size())
        {
            return this.undos.get(this.position);
        }

        return null;
    }

    public int getCurrentUndos()
    {
        return this.position + 1;
    }

    public int getTotalUndos()
    {
        return this.undos.size();
    }

    /**
     * Push the undo, and apply it immediately
     */
    public IUndo<T> pushApplyUndo(IUndo<T> undo, T context)
    {
        IUndo<T> newUndo = this.pushUndo(undo);

        newUndo.redo(context);

        if (this.callback != null)
        {
            this.callback.handleUndo(undo, true);
        }

        return newUndo;
    }

    public IUndo<T> pushUndo(IUndo<T> undo)
    {
        IUndo<T> present = this.position == -1 ? null : this.undos.get(this.position);

        if (present != null && this.checkMergeability(present, undo))
        {
            this.removeConsequent();
            present.merge(undo);
        }
        else
        {
            if (this.position + 1 >= this.limit)
            {
                this.undos.remove(0);
            }
            else
            {
                this.removeConsequent();
                this.position += 1;
            }

            present = undo;
            this.undos.add(undo);
        }

        return present;
    }

    private boolean checkMergeability(IUndo<T> present, IUndo<T> undo)
    {
        if (this.simpleMerge)
        {
            return present.isMergeable(undo);
        }

        return present.isMergeable(undo) && undo.isMergeable(present);
    }

    protected void removeConsequent()
    {
        /* Remove the consequent undos that could've been redone */
        while (this.undos.size() > this.position + 1)
        {
            this.undos.remove(this.undos.size() - 1);
        }
    }

    /**
     * Undo changes done to context
     */
    public boolean undo(T context)
    {
        if (this.position < 0)
        {
            return false;
        }

        IUndo<T> undo = this.undos.get(this.position);

        undo.undo(context);
        this.position -= 1;

        if (this.callback != null)
        {
            this.callback.handleUndo(undo, false);
        }

        return true;
    }

    /**
     * Redo changes done to context
     */
    public boolean redo(T context)
    {
        if (this.position + 1 >= this.undos.size())
        {
            return false;
        }

        IUndo<T> undo = this.undos.get(this.position + 1);

        undo.redo(context);
        this.position += 1;

        if (this.callback != null)
        {
            this.callback.handleUndo(undo, true);
        }

        return true;
    }
}