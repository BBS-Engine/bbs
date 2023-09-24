package mchorse.bbs.ui.film.utils.undo;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.utils.undo.IUndo;

public class ValueChangeUndo extends FilmEditorUndo
{
    public String name;
    public BaseType oldValue;
    public BaseType newValue;

    private boolean mergable = true;

    public ValueChangeUndo(String name, BaseType oldValue, BaseType newValue)
    {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public IUndo<ValueGroup> noMerging()
    {
        this.mergable = false;

        return this;
    }

    @Override
    public boolean isMergeable(IUndo<ValueGroup> undo)
    {
        if (!this.mergable)
        {
            return false;
        }

        if (undo instanceof ValueChangeUndo)
        {
            ValueChangeUndo valueUndo = (ValueChangeUndo) undo;

            return this.name.equals(valueUndo.getName());
        }

        return false;
    }

    @Override
    public void merge(IUndo<ValueGroup> undo)
    {
        if (undo instanceof ValueChangeUndo)
        {
            ValueChangeUndo prop = (ValueChangeUndo) undo;

            this.newValue = prop.newValue;
        }
    }

    @Override
    public void undo(ValueGroup context)
    {
        BaseValue value = context.getRecursively(this.name);

        if (value.getPath().equals(this.name))
        {
            value.fromData(this.oldValue);
        }
    }

    @Override
    public void redo(ValueGroup context)
    {
        BaseValue value = context.getRecursively(this.name);

        if (value.getPath().equals(this.name))
        {
            value.fromData(this.newValue);
        }
    }
}